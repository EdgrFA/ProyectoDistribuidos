create database distribuidos;
use distribuidos;

create table datos(
	c1 int,c2 int,c3 int,c4 int,c5 int,c6 int,c7 int,c8 int,c9 int,c10 int,c11 int,c12 int,c13 int,c14 int,c15 int,c16 int,c17 int,c18 int,c19 int,c20 int,c21 int,c22 int,c23 int,c24 int ,c25 int ,c26 int,c27 int,
	r1 int,r2 int,r3 int,r4 int,r5 int,r6 int,r7 int,r8 int,r9 int,r10 int,r11 int,r12 int,r13 int,r14 int,r15 int,r16 int,r17 int,r18 int,r19 int,r20 int,r21 int,r22 int,r23 int,r24 int ,r25 int ,r26 int,r27 int,
    ip varchar(40),
    hora int,
    minutos int,
    segundos int);

drop table datos;
    
select * from datos;

create database relojServidor;
use relojServidor;

create table ajustes(
    ip varchar(40),
    ajuste int,
    hora int,
    minutos int,
    segundos int);
drop table ajustes;
    
select * from ajustes;