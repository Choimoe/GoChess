import numpy as np


def conv_matrix_2d(matrix, core, step=1):
    matrix_size = matrix.shape
    core_size = matrix.shape

    result = np.empty(int(matrix_size[0] - core_size[0]) // step + 1,
                      int(matrix_size[1] - core_size[1]) // step + 1)

    i = j = 0
    while i < matrix_size[0]:
        while j < matrix_size[1]:
            # conv matrix[i, j]

            if i > matrix_size[0] - core_size[0] or j > matrix_size[0] - core_size[1]:
                break

            temp = 0
            for n in range(core_size[0]):
                for m in range(core_size[1]):
                    temp += core[n, m] * matrix[i + n, j + m]

            result[i, j] = temp
            i += step
            j += step


def conv_point_2d(matrix, point, core):
    core_size = core.shape
    matrix_size = matrix.shape
    radius = core_size // 2
    x, y = point

    result = 0

    for i in range(max(0, x - radius), min(matrix[0], x + radius)):
        for j in range(max(0, y - radius), min(matrix[1], y + radius)):
            result = matrix[i, j] * core[i - x + radius, j - y + radius]

    return result


def re_lu(x):
    return max(0, x)


def construct_core_edge(radius, core_type=1):
    size = 2 * radius - 1
    result = np.empty(size, size)
    core_type = [1, 2, 3, 4][core_type]

    # example = [
    #     [-1, -1, 0, 1, 1],
    #     [-1, -3, 0, 2, 1],
    #     [0, 0, 0, 0, 0],
    #     [-1, -3, 0, 3, 1],
    #     [-1, -1, 0, 1, 1],
    # ]
