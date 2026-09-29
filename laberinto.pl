%coordenadas de los obstaculos dentro del tablero obstaculo(y,x)

obstaculo(1,8).
obstaculo(2,3). obstaculo(2,6). obstaculo(2,10).
obstaculo(3,1). obstaculo(3,3). obstaculo(3,8).
obstaculo(4,3). obstaculo(4,10).
obstaculo(5,5). obstaculo(5,7). obstaculo(5,8).
obstaculo(6,3). obstaculo(6,4). obstaculo(6,6). obstaculo(6,10).
obstaculo(7,1). obstaculo(7,2). obstaculo(7,4).
obstaculo(8,10).
obstaculo(9,3). obstaculo(9,4). obstaculo(9,5). obstaculo(9,7). obstaculo(9,8).

%casillas con costos especiales costo_especial(y,x,costo)

costo_especial(1,3,4). costo_especial(1,5,2).
costo_especial(2,1,4). costo_especial(2,5,3). costo_especial(2,7,2).
costo_especial(3,4,2). costo_especial(3,6,4). costo_especial(3,7,3).
costo_especial(4,1,3). costo_especial(4,2,3). costo_especial(4,6,3). costo_especial(4,9,2).
costo_especial(5,2,5). costo_especial(5,3,3).
costo_especial(6,5,2). costo_especial(6,8,4).
costo_especial(7,6,5). costo_especial(7,7,2).
costo_especial(8,2,3). costo_especial(8,4,2). costo_especial(8,6,3). costo_especial(8,7,2). costo_especial(8,9,3).
costo_especial(10,3,3). costo_especial(10,5,3). costo_especial(10,9,2).


%regla de costos: si la casilla no se encuentra dentro de los hechos costo_especial el costo es 1
costo(F,C,K) :- costo_especial(F,C,K),!.
costo(_,_,1).

%regla de casilla valida: si la casilla se encuentra dentro de los limites del tablero y no es un obstaculo se considera que es valida para su eleccion

c_valida(F,C) :- between(1,10,F), between(1,10,C), \+obstaculo(F,C).