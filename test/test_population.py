import unittest

from src.population import *

class TestPopulation(unittest.TestCase):
    def test_representation(self):
        self.assertEqual(repr(Population([Program(42), Program(('+', 2, 1))], size=5, generation=10)),
                         "Population({Program(42), Program(('+', 2, 1))}, size=5, generation=10)")

    def test_pair_forming_uses_programs_correctly(self):
        programs = [
            Program(('*', 2, ('*', 3, ('*', 5, 6)))),
            Program(('+', 2, ('+', 3, 4))),
        ]
        self.assertEqual(Population._form_pairs(programs),
                         [{Program(('*', 2, ('*', 3, ('*', 5, 6)))), Program(('+', 2, ('+', 3, 4)))}])

    def test_pair_forming_handles_populations_with_odd_size(self):
        programs = [
            Program(42),
            Program(('*', 2, ('*', 3, ('*', 5, 6)))),
            Program(('+', 2, ('+', 3, 4))),
            Program(('-', 2, 1)),
            Program(('/', 4, 2)),
        ]
        self.assertEqual(len(Population._form_pairs(programs)), 2)

    def test_task_fittest(self):
        self.assertEqual(Population._take_fittest([Program(1), Program(3), Program(2)], lambda x: x.expression, 2),
                         [Program(1), Program(2)])

    def test_next_generation(self):
        population = Population([
            Program(42),
            Program(('*', 2, ('*', 3, ('*', 5, 6)))),
            Program(('+', 2, ('+', 3, 4))),
            Program(('-', 2, 1)),
        ])
        new_population = population.next_generation(lambda _: 1, lambda x: x, 0.5)

        self.assertEqual(new_population.generation, 1)

    def test_next_generation_size_is_enforced(self):
        population = Population([Program(1), Program(2)], size = 3)
        new_population = population.next_generation(lambda x: x.expression, lambda x: x + 1, 0.5)

        self.assertEqual(new_population.programs, {Program(1), Program(2), Program(3)})

if __name__ == '__main__':
    unittest.main()
