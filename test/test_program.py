import unittest

from src.program import *

class TestProgram(unittest.TestCase):
    def test_hashing(self):
        self.assertEqual(hash(Program('x')), hash(Program('x')))

    def test_equality(self):
        self.assertEqual(Program('x'), Program('x'))
        self.assertEqual(Program(('+', 1, 2)), Program(('+', 1, 2)))
        self.assertEqual(Program(('+', 1, ('f', 2))), Program(('+', 1, ('f', 2))))

        self.assertNotEqual(Program('x'), Program('y'))
        self.assertNotEqual(Program(('+', 'a', 'b')), Program(('+', 'a', 'b', 'c')))
        self.assertNotEqual(Program(('+', 'a', 'b')), Program(('+', 'b', 'a')))
        self.assertNotEqual(Program(('+', 'a', 'b')), Program(('+', 'a', 'c')))
        self.assertNotEqual(Program(('+', 1, ('f', 2))), Program(('+', 1, ('f', 3))))

    def test_invocation(self):
        self.assertEqual(Program(42)(), 42)
        self.assertEqual(Program((sum, [1, 2, 3]))(), 6)
        self.assertEqual(Program((int.__add__, 2, 3))(), 5)
        self.assertEqual(Program((int.__mul__, 2, (int.__add__, 1, 2)))(), 6)

    def test_representation(self):
        self.assertEqual(repr(Program(42)), 'Program(42)')

    def test_crossover_can_be_called(self):
        self.assertEqual(Program('x').crossover(Program('y')), (Program('y'), Program('x')))

    def test_mutate_can_be_called(self):
        self.assertEqual(Program(('+', 1, 2)).mutate(lambda x: x), Program(('+', 1, 2)))

    def test_set_membership(self):
        self.assertIn(Program(('+', 1, 2)), {Program(('+', 1, 2))})
        self.assertNotIn(Program(('+', 1, 2)), {Program(('+', 1, 3))})

if __name__ == '__main__':
    unittest.main()
