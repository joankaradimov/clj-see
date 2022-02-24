from functools import cached_property
from random import choice

from src.expression import *

def eval_expression(expression, **kwargs):
    if isinstance(expression, tuple):
        first, *remaning = expression
        return first(*[eval_expression(x, **kwargs) for x in remaning])
    elif isinstance(expression, str):
        return kwargs[expression]
    else:
        return expression

class Program:
    def __init__(self, expression = None):
        self.expression = expression

    def __hash__(self):
        return hash(self.expression)

    def __eq__(self, other):
        return isinstance(other, Program) and self.expression == other.expression

    def __repr__(self):
        return f'Program({repr(self.expression)})'

    def __call__(self, **kwargs):
        return eval_expression(self.expression, **kwargs)

    @cached_property
    def all_paths(self):
        return all_paths(self.expression)

    def random_path(self):
        return choice(self.all_paths)

    def crossover(self, other):
        e1, e2 = crossover(self.expression, self.random_path(), other.expression, other.random_path())

        return Program(e1), Program(e2)

    def mutate(self, mutation_function):
        path = self.random_path()
        new_expression = mutate(self.expression, path, mutation_function)

        return Program(new_expression)
