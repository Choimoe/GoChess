import numpy as np

PROD_EPS = 30.0
COS_ANGEL_TIMES = 0.6
XY_EPS = PROD_EPS * 0.2


# remove multi-polyline

def make_vector(line):
    x1, y1, x2, y2 = line[0]
    return np.array([x1 - x2, y1 - y2])


def make_vec(x1, y1):
    return np.array([x1, y1])


def vec_prod(vec1, vec2):
    x1, y1 = vec1
    x2, y2 = vec2
    return x1 * y2 - x2 * y1


def vector_in_prod(vec1, vec2):
    x1, y1 = vec1
    x2, y2 = vec2
    return x1 * x2 + y1 * y2


def vec_length(vec):
    return np.sqrt(vector_in_prod(vec, vec))


def point_distance(x1, y1, x2, y2):
    return np.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))


def vector_length(x1, y1):
    return np.sqrt(x1 * x1 + y1 * y1)


def check_height(x1, y1, x2, y2):
    prod = abs(vec_prod(make_vec(x1, y1), make_vec(x2, y2)))
    return prod / vector_length(x1, y1)


def check_same_line(line1, line2):
    """
    If the two lines are parallel and the two points of one line are on the other line, then the two lines are the same

    :param line1: [[x1, y1, x2, y2]]
    :param line2: [[x1, y1, x2, y2]]
    """

    x1, y1, x2, y2 = line1[0]
    x3, y3, x4, y4 = line2[0]
    if (abs(vector_in_prod(make_vector(line1), make_vector(line2)))
            < COS_ANGEL_TIMES * vec_length(make_vector(line1)) * vec_length(make_vector(line2))):
        return 0

    if (abs(check_height(x1 - x2, y1 - y2, x1 - x3, y1 - y3)) - PROD_EPS < 0 and
            abs(check_height(x1 - x2, y1 - y2, x1 - x4, y1 - y4)) - PROD_EPS < 0):
        return 1

    return 0


def line_equals(line1, line2):
    x1, y1, x2, y2 = line1[0]
    x3, y3, x4, y4 = line2[0]
    if x1 != x3 or y1 != y3 or x2 != x4 or y2 != y4:
        return 0
    return 1


def line_cmp(line1, line2):
    """
    If both lines are the same type, sort by x coordinate, otherwise sort by y coordinate

    :param line1: the first line to compare
    :param line2: the line we're comparing to
    :return: a boolean value that if line1 < line2.
    """
    x1, y1, x2, y2 = line1[0]
    x3, y3, x4, y4 = line2[0]
    line_type1 = 1 if abs(x1 - x2) < XY_EPS else -1
    line_type2 = 1 if abs(x3 - x4) < XY_EPS else -1

    if line_type1 + line_type2 == 0:
        return line_type1 == 1

    if line_type1 == 1:
        return x1 < x3
    else:
        return y1 < y3


def partition(li, left, right):
    """
    It partitions the list into two parts, one part is smaller than the pivot, the other part is larger than the pivot

    :param li: the list to be sorted
    :param left: the left index of the list
    :param right: the index of the last element in the list
    :return: The index of the pivot element.
    """
    tmp = li[left]
    while left < right:
        while left < right and not line_cmp(li[right], tmp):
            right -= 1
        li[left] = li[right]

        while left < right and not line_cmp(tmp, li[left]):
            left += 1
        li[right] = li[left]

    li[left] = tmp
    return left


def quick_sort(li, left, right):
    if left < right:  # 至少两个元素
        mid = partition(li, left, right)
        quick_sort(li, left, mid - 1)
        quick_sort(li, mid + 1, right)


def remove_same_line(lines):
    """
    It removes lines that are the same

    :param lines: The output of cv2.HoughLinesP
    :return: The number of lines and the lines themselves.
    """
    basis = []
    count = 0

    for line1 in lines:
        flag = 1

        for line2 in basis:
            if check_same_line(line1, line2) != 1:
                continue

            flag = 0

            if vec_length(make_vector(line1)) > vec_length(make_vector(line2)):
                line2 = line1

            break

        if not flag:
            continue

        count = count + 1
        basis.append(line1)

    quick_sort(basis, 0, len(basis) - 1)

    return count, np.array(basis)
