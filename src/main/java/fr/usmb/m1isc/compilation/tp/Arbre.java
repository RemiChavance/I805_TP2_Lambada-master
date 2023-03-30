package fr.usmb.m1isc.compilation.tp;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class Arbre {

    private TypeNode type;
    private String racine;
    private Arbre fg, fd;

    private final String TABULATION = "\t";
    private final String RETOUR_LIGNE = "\n";

    public Arbre(TypeNode type, String racine, Arbre fg, Arbre fd) {
        this.type = type;
        this.racine = racine;
        this.fg = fg;
        this.fd = fd;
    }

    public Arbre(TypeNode type, String racine) {
        this.type = type;
        this.racine = racine;
    }

    public Arbre() {
    }

    public String getRacine() {
        return this.racine;
    }

    public String toString() {
        String resultat = "";
        if ((this.type == TypeNode.OPERATEUR) || (this.type == TypeNode.LET)) {
            resultat += "(" + this.racine;
        } else {
            resultat += " " + this.racine;
        }
        if (!isNull(this.fg)) {
            resultat += this.fg.toString();
        }
        if (!isNull(this.fd)) {
            resultat += this.fd.toString();
        }
        if ((this.type == TypeNode.OPERATEUR) || (this.type == TypeNode.LET)) {
            resultat += ")";
        }
        return resultat;
    }


    public String generateAllCode() {
        StringBuilder code = new StringBuilder();
        code.append("DATA SEGMENT" + RETOUR_LIGNE);

        List<String> listData = new ArrayList<>();
        this.generateDataSection(listData);

        for (String data : listData) {
            code.append(TABULATION);
            code.append(data);
            code.append(" DD" + RETOUR_LIGNE);
        }

        code.append("DATA ENDS" + RETOUR_LIGNE);
        code.append("CODE SEGMENT" + RETOUR_LIGNE);
        code.append(this.generateCodeSection());
        code.append("CODE ENDS");
        return code.toString();
    }

    private void generateDataSection(List<String> data) {
        if (!isNull(this.fg)) {
            this.fg.generateDataSection(data);
        }
        if (!isNull(this.fd)) {
            this.fd.generateDataSection(data);
        }
        if ((this.type == TypeNode.IDENT) && (!data.contains(this.racine))) {
            data.add(this.racine);
        }
    }

    private String generateCodeSection() {
        String resultat = "";

        // POINT VIRGULE
        if (this.type == TypeNode.SEMI) {
            return this.fg.generateCodeSection() + this.fd.generateCodeSection();
        }

        // ENTIER / IDENT
        else if ((this.type == TypeNode.ENTIER) || (this.type == TypeNode.IDENT)) {
            return TABULATION + "mov eax, " + this.racine + RETOUR_LIGNE;
        }

        // OPERATEUR (+, -, *, /)
        else if (this.type == TypeNode.OPERATEUR) {
            resultat += this.fg.generateCodeSection();
            resultat += TABULATION + "push eax" + RETOUR_LIGNE;
            resultat += this.fd.generateCodeSection();
            resultat += TABULATION + "pop ebx" + RETOUR_LIGNE;
            switch (this.racine) {
                case "+" -> resultat += TABULATION + "add eax, ebx" + RETOUR_LIGNE;
                case "*" -> resultat += TABULATION + "mul eax, ebx" + RETOUR_LIGNE;
                case "-" -> {
                    resultat += TABULATION + "sub ebx, eax" + RETOUR_LIGNE;
                    resultat += TABULATION + "mov eax, ebx" + RETOUR_LIGNE;
                }
                case "/" -> {
                    resultat += TABULATION + "div ebx, eax" + RETOUR_LIGNE;
                    resultat += TABULATION + "mov eax, ebx" + RETOUR_LIGNE;
                }
            }
            return resultat;
        }

        // LET
        else if (this.type == TypeNode.LET) {
            resultat += this.fd.generateCodeSection();
            resultat += TABULATION + "mov " + this.fg.racine + ", eax" + RETOUR_LIGNE;
            return resultat;
        }

        return resultat;
    }
}
