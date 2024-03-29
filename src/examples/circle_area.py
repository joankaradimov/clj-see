import math
import random

def circle_area(radius):
    return math.pi * radius ** 2

def fitness(program):
    program_size = len(program.all_paths)
    diff_function = lambda r: circle_area(r) - program(r=r)

    return math.log(program_size + 1) * sum(diff_function(float(i)) ** 2 for i in range(30))

def try_eval(expression):
    try:
        return expression()
    except:
        return expression

non_terminal = [
    lambda e: e,
    lambda e: random.choice(e[1:]),
    lambda e: random.choice(e[1:]),
    lambda e: random.choice(e[1:]),
    lambda e: try_eval(e),
    lambda e: (float.__add__, e, 0.0),
    lambda e: (float.__sub__, 0.0, e),
    lambda e: (float.__mul__, 1.0, e),
    # lambda e: (float.__pow__, e, 1.0),
    # lambda e: (math.exp, e),
    # lambda e: (math.log, e, 1.0),
]

terminal = [
    lambda e: e,
    lambda e: random.random(),
    lambda e: random.random(),
    lambda e: random.random(),
    lambda e: 'r',
    lambda e: (float.__add__, e, 0.0),
    lambda e: (float.__mul__, 1.0, e),
]

def mutate(expression):
    mutations = non_terminal if isinstance(expression, tuple) else terminal
    return random.choice(mutations)(expression)
