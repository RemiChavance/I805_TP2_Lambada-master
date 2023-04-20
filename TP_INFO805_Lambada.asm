DATA SEGMENT
	a DD
	b DD
	aux DD
DATA ENDS
CODE SEGMENT
	in eax
	mov a, eax
	push eax
	in eax
	mov b, eax
	push eax
debut_while_1:
	mov eax, 0
	push eax
	mov eax, b
	pop ebx
	sub eax, ebx
	jge faux_gt_1
	mov eax, 1
	jmp sortie_gt_1
faux_gt_1 :
	mov eax, 0
sortie_gt_1 :
	jz sortie_while_1
	mov eax, b
	push eax
	mov eax, a
	pop ebx
	mov ecx, eax
	div ecx, ebx
	mul ecx, ebx
	sub eax, ecx
	mov aux, eax
	push eax
	mov eax, b
	mov a, eax
	push eax
	mov eax, aux
	mov b, eax
	pop ebx
	pop ebx
	jmp debut_while_1
sortie_while_1:
	push eax
	mov eax,  a
	out eax
	pop ebx
	pop ebx
	pop ebx
CODE ENDS