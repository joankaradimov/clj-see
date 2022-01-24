from src.util import *

def crossover(expression1, path1, expression2, path2):
    snippet1 = get_in(expression1, path1)
    snippet2 = get_in(expression2, path2)
    new_expression1 = assoc_in(expression1, path1, snippet2)
    new_expression2 = assoc_in(expression2, path2, snippet1)

    return new_expression1, new_expression2

def mutate(expression, path, mutate):
    snippet = get_in(expression, path)
    mutated_snippet = mutate(snippet)
    new_expression = assoc_in(expression, path, mutated_snippet)

    return new_expression
