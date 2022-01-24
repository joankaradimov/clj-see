import unittest
from src.expression import *

class TestExpression(unittest.TestCase):
    def test_crossover(self):
        self.assertEqual(crossover(('+', 1, ('chiasma-1', 42, ('*', 1, 2))), [2],
                                   ('*', 2, 3, ('+', 1, ('chiasma-2', 123), 3)), [3, 2]),
                         (
                             ('+', 1, ('chiasma-2', 123)),
                             ('*', 2, 3, ('+', 1, ('chiasma-1', 42, ('*', 1, 2)), 3))
                         ))

    def test_mutation(self):
        self.assertEqual(mutate(('+', ('*', 'a', 'x', 'x'), ('*', 'b', 'x'), 'c'), [1, 3], lambda _: 'y'),
                         ('+', ('*', 'a', 'x', 'y'), ('*', 'b', 'x'), 'c'))

if __name__ == '__main__':
    unittest.main()
