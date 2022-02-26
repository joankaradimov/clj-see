import os

from examples import circle_area
from program import Program
from population import Population

population_size = 250
filename_prefix = os.path.join("examples-output", "circle-area-py", "iter")

population = Population([Program(i / population_size) for i in range(population_size)], 0)

while True:
    population = population.next_generation(circle_area.fitness, circle_area.mutate, 0.1)
    if population.generation % 100 == 0:
        print(population)
