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
        StringBuilder resultat = new StringBuilder();

        if (this.type == TypeNode.SEMI) {
            return this.fg.generateCodeSection() + this.fd.generateCodeSection();
        }
        if (this.type == TypeNode.ENTIER || this.type == TypeNode.IDENT) {
            resultat.append("\tmov eax, ").append(this.racine).append("\n");
        } else if (this.type == TypeNode.OPERATEUR) {
            resultat.append(this.fg.generateCodeSection());
            resultat.append("\tpush eax\n");
            resultat.append(this.fd.generateCodeSection());
            resultat.append("\tpop ebx\n");
            switch (this.racine) {
                case "+":
                    resultat.append("\tadd eax, ebx\n");
                    break;
                case "-":
                    resultat.append("\tsub ebx, eax\n");
                    resultat.append("\tmov eax, ebx\n");
                    break;
                case "*":
                    resultat.append("\tmul eax, ebx\n");
                    break;
                case "/":
                    resultat.append("\tdiv ebx, eax\n");
                    resultat.append("\tmov eax, ebx\n");
                    break;
                default:
                    break;
            }
        } else if (this.type == TypeNode.LET) {
            resultat.append(this.fd.generateCodeSection());
            resultat.append("\tmov ").append(this.fg.racine).append(", eax\n");
        } else if (this.type == TypeNode.INPUT) {
            resultat.append("\tin eax\n");
        } else if (this.type == TypeNode.OUTPUT) {
            resultat.append("\tmov eax, ").append(this.racine).append("\n");
            resultat.append("\tout eax\n");
        }else if (this.type == TypeNode.LT) {
            resultat.append(this.fg.generateCodeSection());
            resultat.append( "\tpush eax\n");
            resultat.append( this.fd.generateCodeSection());
            resultat.append( "\tpop ebx\n");
            resultat.append( "\tsub eax, ebx\n");
            resultat.append( "\tjle faux_lt_1\n");
            resultat.append( "\tmov eax, 1\n");
            resultat.append( "\tjmp sortie_lt_1\n");
            resultat.append( "faux_lt_1 :\n");
            resultat.append( "\tmov eax, 0\n");
            resultat.append( "sortie_lt_1 :\n");
            return resultat.toString();
        }

        else if (this.type == TypeNode.LTE) {
            resultat.append( this.fg.generateCodeSection());
            resultat.append( "\tpush eax\n");
            resultat.append( this.fd.generateCodeSection());
            resultat.append( "\tpop ebx\n");
            resultat.append( "\tsub eax, ebx\n");
            resultat.append( "\tjl faux_lte_1\n");
            resultat.append( "\tmov eax, 1\n");
            resultat.append( "\tjmp sortie_lte_1\n");
            resultat.append( "faux_lte_1 :\n");
            resultat.append( "\tmov eax, 0\n");
            resultat.append( "sortie_lte_1 :\n");
            return resultat.toString();
        }
        else if (this.type == TypeNode.GT) {
            resultat.append(this.fg.generateCodeSection());
            resultat.append("\tpush eax\n");
            resultat.append(this.fd.generateCodeSection());
            resultat.append("\tpop ebx\n");
            resultat.append("\tsub eax, ebx\n");
            resultat.append("\tjge faux_gt_1\n");
            resultat.append("\tmov eax, 1\n");
            resultat.append("\tjmp sortie_gt_1\n");
            resultat.append("faux_gt_1 :\n");
            resultat.append("\tmov eax, 0\n");
            resultat.append("sortie_gt_1 :\n");
            return resultat.toString();
        }

        else if (this.type == TypeNode.GTE) {
            resultat.append(this.fg.generateCodeSection());
            resultat.append("\tpush eax\n");
            resultat.append(this.fd.generateCodeSection());
            resultat.append("\tpop ebx\n");
            resultat.append("\tsub eax, ebx\n");
            resultat.append("\tjg faux_gte_1\n");
            resultat.append("\tmov eax, 1\n");
            resultat.append("\tjmp sortie_gte_1\n");
            resultat.append("faux_gte_1 :\n");
            resultat.append("\tmov eax, 0\n");
            resultat.append("sortie_gte_1 :\n");
            return resultat.toString();
        }
        else if (this.type == TypeNode.EGAL) {
            resultat.append( this.fg.generateCodeSection());
            resultat.append( "\tpush eax\n");
            resultat.append( this.fd.generateCodeSection());
            resultat.append( "\tpop ebx\n");
            resultat.append( "\tsub eax, ebx\n");
            resultat.append( "\tjnz faux_egal_1\n");
            resultat.append( "\tmov eax, 1\n");
            resultat.append( "\tjmp sortie_egal_1\n");
            resultat.append( "faux_egal_1 :\n");
            resultat.append( "\tmov eax, 0\n");
            resultat.append( "sortie_egal_1 :\n");
            return resultat.toString();
        }
        else if (this.type == TypeNode.MOD) {
            resultat.append( this.fd.generateCodeSection());
            resultat.append( "\tpush eax\n");
            resultat.append( this.fg.generateCodeSection());
            resultat.append( "\tpop ebx\n");
            resultat.append( "\tmov ecx, eax\n");
            resultat.append( "\tdiv ecx, ebx\n");
            resultat.append( "\tmul ecx, ebx\n");
            resultat.append( "\tsub eax, ecx\n");
            return resultat.toString();
        }
        else if(this.type == TypeNode.WHILE){
            resultat.append( "debut_while_1:\n");
            resultat.append( this.fg.generateCodeSection());
            resultat.append( "\tjz sortie_while_1\n");
            resultat.append( this.fd.generateCodeSection());
            resultat.append( "\tjmp debut_while_1\n");
            resultat.append( "sortie_while_1:\n");
            return resultat.toString();
        }

        else if (this.type == TypeNode.IF) {
            resultat.append( this.fg.generateCodeSection());
            resultat.append( "\tjz faux_if_1\n");
            resultat.append( this.fd.fg.generateCodeSection());
            resultat.append( "\tjmp sortie_if_1\n");
            resultat.append( "faux_if_1 :\n");
            resultat.append( this.fd.fd.generateCodeSection());
            resultat.append( "sortie_if_1 :\n");
            return resultat.toString();
        }

        else if (this.type == TypeNode.AND) {
            resultat.append( this.fg.generateCodeSection());
            resultat.append( "\tjz faux_and_1\n");
            resultat.append( this.fd.generateCodeSection());
            resultat.append( "faux_and_1 :\n");
            return resultat.toString();
        }


        else if (this.type == TypeNode.OR) {
            resultat.append( this.fg.generateCodeSection());
            resultat.append( "\tjnz vrai_or_1\n");
            resultat.append( this.fd.generateCodeSection());
            resultat.append( "\tjnz vrai_or_1\n");
            resultat.append( "vrai_or_1 :\n");
            return resultat.toString();
        }

        else if (this.type == TypeNode.NOT) {
            resultat.append( this.fg.generateCodeSection());
            resultat.append( "\tjnz faux_not_1\n");
            resultat.append( "\tmov eax, 1\n");
            resultat.append( "\tjmp sortie_not_1\n");
            resultat.append( "faux_not_1 :\n");
            resultat.append( "\tmov eax, 0\n");
            resultat.append( "sortie_not_1 :\n");
            return resultat.toString();
        }

        return resultat.toString();
    }
}
