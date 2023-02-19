import numpy as np
import cv2
import socket


def get_avg_color(image, x, y, radius):
    """
    It takes an image, a point, and a radius, and returns the average color of the pixels in the image within the radius
    of the point

    :param image: the image to be processed
    :param x: x coordinate of the center of the circle
    :param y: the y coordinate of the center of the circle
    :param radius: The radius of the circle around the pixel that we want to average the color of
    :return: The average color of the image.
    """
    rad = radius
    color_sum = 0
    cnt = 0
    img_size = image.shape
    x_limit = img_size[0]
    y_limit = img_size[1]
    for i in range(max(0, x - rad), min(x + rad, x_limit - 1)):
        for j in range(max(0, y - rad), min(y + rad, y_limit - 1)):
            cnt = cnt + 1
            for k in image[j, i]:
                color_sum += k
    if cnt == 0:
        return -1
    return color_sum // cnt


def make_general_equation(x1, y1, x2, y2):
    return y2 - y1, x1 - x2, x2 * y1 - x1 * y2


def get_intersect_point_of_lines(line1, line2):
    """
    Given two lines, find the point of intersection

    :param line1: [[x1, y1, x2, y2]]
    :param line2: [[x1, y1, x2, y2]]
    :return: The x and y coordinates of the intersection point of the two lines.
    """
    x1, y1, x2, y2 = line1[0]
    x3, y3, x4, y4 = line2[0]
    a1, b1, c1 = make_general_equation(x1, y1, x2, y2)
    a2, b2, c2 = make_general_equation(x3, y3, x4, y4)
    m = a1 * b2 - a2 * b1
    x = (c2 * b1 - c1 * b2) / m
    y = (c1 * a2 - c2 * a1) / m
    return x, y


PROD_EPS = 30.0


def get_board_avg_color(image, lines, xy_eps=PROD_EPS * 0.2):
    """
    It finds the average color of the board

    :param image: the image to be processed
    :param lines: the lines of the board
    :param xy_eps: the radius of the circle around the intersection point of the lines
    :return: The average color of the board.
    """
    row = 19
    color_sum = 0
    cnt = 0
    for i in range(row, 2 * row):
        for j in range(0, row):
            x, y = get_intersect_point_of_lines(lines[i], lines[j])
            x = int(x)
            y = int(y)
            color = get_avg_color(image, x, y, int(xy_eps))
            color_sum += color
            cnt += 1
    if cnt == 0:
        return 250
    return color_sum // cnt


def lower_white_piece(feature):
    return (feature * 2 + 255 * 3) // 3


def upper_black_piece(feature):
    return (feature * 2 + 0) // 3


def equals(a, b):
    return abs(a - b) < 0.0001


def trans_go_map(image, lines, feature, temperature, sock, xy_eps=PROD_EPS * 0.2):
    """
    It takes an image, a list of lines, and a feature, and returns a go map and a result image

    :param temperature: the time of waiting to ensure the piece
    :param image: the image to be processed
    :param lines: the lines that are detected by the Hough transform
    :param feature: the average color of the board
    :param xy_eps: the radius of the circle to get the average color
    :return: The go_map is a 19x19 matrix that contains the values of the intersections of the lines.
            The result is the image with the intersections marked.
    """
    original_image = image.copy()
    result = image.copy()
    row = 19
    go_map = np.empty((19, 19))
    for i in range(row):
        for j in range(row):
            go_map[i, j] = 0
    for i in range(row, 2 * row):
        for j in range(0, row):
            x, y = get_intersect_point_of_lines(lines[i], lines[j])
            x = int(x)
            y = int(y)

            if temperature[i - row, j] == 0:
                cv2.circle(result, (x, y), 20, (0, 0, 255), 2)
                continue

            color = get_avg_color(original_image, x, y, int(xy_eps))
            # print(posX, posY, rad, image[posY, posX])
            # print('(', x, y, ')', color, end = ',')
            # print(original_image[posX, posY])
            if color == -1:
                temperature[i - row, j] = 20
                continue

            if color < upper_black_piece(feature) or color > lower_white_piece(feature):
                temperature[i - row, j] -= 1

            if not temperature[i - row, j] == 0:
                continue

            sng = str(i - row) + " " + str(j)

            if color < upper_black_piece(feature):
                go_map[i - row, j] = 1
                sng = sng + " B\n"
                sock.send(bytes(sng, 'utf-8'))
                # sock.send(b"1 1 B")
                cv2.circle(result, (x, y), 20, (0, 255, 0), 2)
            if color > lower_white_piece(feature):
                go_map[i - row, j] = -1
                sng = sng + " W\n"
                sock.send(bytes(sng, 'utf-8'))
                # sock.send(b"1 1 W")
                cv2.circle(result, (x, y), 20, (255, 0, 0), 2)

    return go_map, result
