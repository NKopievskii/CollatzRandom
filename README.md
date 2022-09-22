# Pseudorandom number generator on Collatz hypothesis
A random number generator based on the Collatz hypothesis. It is an algorithm for creating sequences of random numbers necessary for a variety of mathematical calculations and other purposes.
## Description
The ***random*** number generator uses an algorithm for generating **pseudo-random** numbers, which is based on the 3n+1 hypothesis. An audio file is used as a source of entropy. The purpose of the development was to create an algorithm for generating pseudorandom numbers, and the source of entropy was a necessary by-product and can be replaced.
### Description of the generator
The algorithm itself is implemented in the RandomCollatz class and does not depend on the Random class, since these classes use different data types as the main output parameter (in Random it is int, in RandomCollatz it is long), although they can easily be modified if necessary. This is due to the fact that 63 bits were used in the developed generator for a longer sequence period. The developed generator also provides the possibility of multithreading, unlike the standard generator.
____
# Генератор псевдослучайных чисел основанный на гипотизе Коллатца
Генератор случайных чисел, разработанный на основе гипотезы Коллатца. Представляет собой алгоритм для создания последовательностей случайных чисел, необходимых для множества математических расчётов и других целей.
## Описание
Генератор ***случайных*** чисел использует специально разработанный алгоритм генерации **псевдослучайных** чисел, который основывается на гипотезе 3n+1. В качестве источника энтропии используется звуковой файл. Целью разработки было создание алгоритма генерации псевдослучайных чисел, а источник энтропии являлся необходимым побочным продуктом и в случае необходимости может быть заменён.
### Описание генератора
Сам алгоритм реализован в классе RandomCollatz и не зависит от класса Random, так как данные классы используют разные типы данных в качестве основного выходного параметра (в Random это int, в RandomCollatz - long), хотя легко могут быть модифицированы в случае необходимости. Это обуславливается тем, что в разработанном генераторе для более длинного периода последовательности использовались 63 бита. Так же разработанный генератор обеспечивает возможность многопоточности, в отличии от стандартного генератора.
____
