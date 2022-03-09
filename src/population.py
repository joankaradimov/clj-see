from itertools import chain, pairwise
from random import sample

from src.program import *

class Population:
    def __init__(self, programs = [], size = None, generation = 0):
        self.programs = set(programs)
        self.size = size or len(self.programs)
        self.generation = generation

    def __repr__(self):
        return f'Population({self.programs}, size={self.size}, generation={self.generation})'

    def next_generation(self, fitness_function, mutate_function, elitism):
        preserved_programs_count = int(self.size * elitism)
        preserved_programs = self._take_fittest(self.programs, fitness_function, preserved_programs_count)

        new_programs_count = self.size - preserved_programs_count
        # TODO: figure out a better way to sort/pick pairs (maybe based on dissimilarity)
        shuffled_programs = sample(self.programs, self.size) if len(self.programs) > self.size else list(self.programs)
        recommbined_programs = chain(*[p1.crossover(p2) for p1, p2 in self._form_pairs(shuffled_programs)])
        mutated_programs = [p.mutate(mutate_function) for p in recommbined_programs]
        new_programs = self._take_fittest(mutated_programs, fitness_function, new_programs_count)

        return Population(preserved_programs + new_programs, self.size, self.generation + 1)

    def dump(self, filename_prefix):
        pass # TODO: implement

    def load(self, filename_prefix):
        pass # TODO: implement

    @staticmethod
    def _form_pairs(programs):
        return [set(pair) for pair in zip(programs[0::2], programs[1::2])]

    @staticmethod
    def _take_fittest(programs, fitness_function, count):
        # TODO: do not sort the entire array
        return sorted(programs, key=fitness_function)[:count]
