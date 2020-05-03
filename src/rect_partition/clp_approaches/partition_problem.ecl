:- lib(ic).
:- lib(listut).
:- lib(branch_and_bound).
:- lib(ic_global).

:- compile("prolog_data.tmp").

partition_problem(S, Selection, Choice, Search) :-
    generate_vert_list(Verts),
    generate_vars_list(Vars, Verts),
    Vars #:: 0..1,
    generate_rectangle_list(GRects),
    set_rect_constraints(Vars, GRects),
    set_choose_cost_constraints(Vars, Cost),
    minimize(search([Cost|Vars], 0, Selection, Choice, Search, []), Cost),
    get_chosen_verts(Vars, S).

partition_color_problem(C, S, Selection, Choice, Search) :-
    generate_vert_list(Verts),
    generate_vars_list(Vars, Verts),
    partition_problem(S, Selection, Choice, Search),
    length(S, MaxColor),
    Vars #:: 0..MaxColor,
    length(Verts, NVerts),
    set_color_domain_constraints(NVerts, Vars, S),
    generate_rectangle_list(GRects),
    set_color_constraints(GRects, S, Vars),
    Cost #= sum(Vars),
    minimize(search([Cost|Vars], 0, Selection, Choice, Search, []), Cost),
    C = Vars.




set_color_constraints([], _, _).
set_color_constraints([R|T], S, Vars) :-
    r(R, _, RVerts),                                % Get the rectangle verts
    intersection(RVerts, S, Common),                % Get the chosen verts that are in this rectangle
    get_vars(Common, Vars, RVars),                  % Get the chosen vars in this rectangle
    ic_global:alldifferent(RVars),                  % Set the constraints
    set_color_constraints(T, S, Vars).              % Keep traversing


set_color_domain_constraints(0, _, _) :- !.
set_color_domain_constraints(V, Vars, S) :-       % If S contains the vert V, must be different than 0, otherwise it must be 0
    member(V, S),
    get_var(Vars, V, Var),
    Var #\= 0,
    V1 is V-1,
    set_color_domain_constraints(V1, Vars, S),
    !.
set_color_domain_constraints(V, Vars, S) :-
    get_var(Vars, V, Var),
    Var #= 0,
    V1 is V-1,
    set_color_domain_constraints(V1, Vars, S).

generate_vert_list(L) :- 
    findall(X, v(X,_,_), L).

generate_vars_list(Vars, L1) :-
    length(L1, N),
    length(Vars, N).

generate_rectangle_list(L) :- 
    goal(L).

set_rect_constraints(_, []).
set_rect_constraints(Vars, [R|T]) :-
    r(R, _, RVerts),                            % Get the verts that touch the rectangle
    get_vars(RVerts, Vars, RVars),              % Get the variables that correspond to the RVerts
    sum(RVars) #> 0,                            % The sum of their values must be 1
    set_rect_constraints(Vars, T).              % Set constraints for the rest of the rectangles

set_choose_cost_constraints(Vars, Cost) :-
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

test :-
    generate_vert_list(Verts),
    generate_vars_list(Vars, Verts),
    partition_problem(S),
    write(S),
    length(S, MaxColor),
    Vars #:: 0..MaxColor,
    length(Verts, NVerts),
    set_color_domain_constraints(NVerts, Vars, S),
    generate_rectangle_list(GRects),
    set_color_constraints(GRects, S, Vars),
    Cost #= sum(Vars),
    minimize(search([Cost|Vars], 0, input_order, indomain, complete, []), Cost).