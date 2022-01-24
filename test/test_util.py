import unittest
from src.util import *

class TestUtil(unittest.TestCase):
    def test_all_paths(self):
        self.assertEqual(all_paths(42), [[]])
        self.assertEqual(all_paths(('inc', 42)), [[], [1]])
        self.assertEqual(all_paths(('+', 42, 35)), [[], [1], [2]])
        self.assertEqual(all_paths(('+', 42, ('something'))), [[], [1], [2]])
        self.assertEqual(all_paths(('+', 42, ('something', 'else'))), [[], [1], [2], [2, 1]])
        self.assertEqual(all_paths(('+', 42, ('*', 7, 8))), [[], [1], [2], [2, 1], [2, 2]])

    def test_get_in(self):
        self.assertEqual(get_in(('*', 2, ('+', 1, 2)), []), ('*', 2, ('+', 1, 2)))
        self.assertEqual(get_in(('*', 2, ('+', 1, 2)), [2]), ('+', 1, 2))
        self.assertEqual(get_in(('*', 2, ('inc', ('inc', 42))), [2, 1, 1]), 42)

    def test_assoc_in(self):
        self.assertEqual(assoc_in(('*', 2, ('+', 1, 2)), [], ('inc', 42)), ('inc', 42))
        self.assertEqual(assoc_in(('*', 2, ('+', 1, 2)), [2, 2], ('/', 42, 21)), ('*', 2, ('+', 1, ('/', 42, 21))))

if __name__ == '__main__':
    unittest.main()
