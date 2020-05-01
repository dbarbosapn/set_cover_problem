:- lib(ic).
:- lib(listut).
:- lib(branch_and_bound).

:- compile("prolog_data.tmp").

partition_problem(S) :-
    generate_vert_list(Verts),
    generate_vars_list(Vars, Verts),
    Vars #:: 0..1,
    generate_rectangle_list(GRects),
    set_constraints(Vars, GRects),
    set_cost_constraints(Vars, Cost),
    minimize(search([Cost|Vars], 0, input_order, indomain, complete, []), Cost),
    get_chosen_verts(Vars, S).

generate_vert_list(L) :- 
    findall(X, v(X,_,_), L).

generate_vars_list(Vars, L1) :-
    length(L1, N),
    length(Vars, N).

generate_rectangle_list(L) :- 
    goal(L).

set_constraints(_, []).
set_constraints(Vars, [R|T]) :-
    r(R, _, RVerts),                            % Get the verts that touch the rectangle
    get_vars(RVerts, Vars, RVars),              % Get the variables that correspond to the RVerts
    sum(RVars) #> 0,                            % The sum of their values must be 1
    set_constraints(Vars, T).                   % Set constraints for the rest of the rectangles

set_cost_constraints(Vars, Cost) :-
    length(Vars, N),
    Cost #:: 0..N,
    sum(Vars) #= Cost.

get_chosen_verts(Verts, S) :-
    findall(X, nth1(X, Verts, 1), S).

get_vars([],_,[]).
get_vars([I|RIs], Vars, [VarI|RVarsIs]) :-
    get_var(Vars, I, VarI),
    get_vars(RIs, Vars, RVarsIs).

get_var([X|_], 1, X) :- !.
get_var([_|R], I, X) :-
    I1 is I-1,
    get_var(R, I1, X).