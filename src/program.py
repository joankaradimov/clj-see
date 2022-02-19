from functools import cached_property
from random import choice

from src.expression import *

def eval_expression(expression):
    if not isinstance(expression, tuple):
        return expression
    elif callable(expression[0]):
        first, *remaning = expression
        return first(*[eval_expression(x) for x in remaning])
    else:
        return expression

class Program:
    def __init__(self, expression):
        self.expression = expression

    def __hash__(self):
        return hash(self.expression)

    def __eq__(self, other):
        return isinstance(other, Program) and self.expression == other.expression

    def __repr__(self):
        return f'Program({repr(self.expression)})'

    def __call__(self):
        return eval_expression(self.expression)

    @cached_property
    def all_paths(self):
        return all_paths(self.expression)

    def random_path(self):
        return choice(self.all_paths)

    def crossover(self, other):
        e1, e2 = crossover(self.expression, self.random_path(), other.expression, other.random_path())

        return Program(e1), Program(e2)

    def mutate(self, path, mutation_function):
        path = self.random_path()
        new_expression = mutate(self.expression, path, mutation_function)

        return Program(new_expression)
