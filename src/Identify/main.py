import os
import cv2
import numpy as np
import socket

import chessPoly
import checkPos

PROD_EPS: float = 30.0
lineCount: int = 0
width, height = 960, 1036


def get_chess_line(input_image):
    """
    It takes an image, converts it to grayscale, finds the edges, and then finds lines in the image

    :param input_image: the image we're searching in
    :return: a list of lines. Each line is represented by a list of 4 numbers.
    """
    image = input_image.copy()
    # img_size = image.shape

    # Change color to RGB (from BGR)
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    gray_img = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)

    low_threshold = 100
    high_threshold = 200
    edges = cv2.Canny(gray_img, low_threshold, high_threshold)

    rho = 1
    theta = np.pi / 180
    threshold = 50
    min_line_length = PROD_EPS
    max_line_gap = 3

    # creating an image copy to draw lines on
    # line_image = np.copy(image)

    # Run Hough on the edge-detected image
    lines = cv2.HoughLinesP(edges, rho, theta, threshold, np.array([]), min_line_length, max_line_gap)

    return chessPoly.remove_same_line(lines)


position = []


def initialize_transform():
    position.sort(key=lambda x: x[0] + x[1] * 2)

    mat_from = np.float32(position)
    mat_to = np.float32([[0, 0], [width, 0], [0, height], [width, height]])
    trans = cv2.getPerspectiveTransform(mat_from, mat_to)

    return trans


def initialize_position(camera):
    flag = 0
    cv2.namedWindow("initialize")

    res, image = camera.read()

    def on_EVENT_LBUTTONDOWN(event, x, y, flags, param):
        if event == cv2.EVENT_LBUTTONDOWN:
            position.append((x, y))
            cv2.circle(image, (x, y), 3, (255, 0, 0), thickness=-1)

    cv2.setMouseCallback("initialize", on_EVENT_LBUTTONDOWN)

    cv2.imshow("initialize", image)
    while True:
        k = cv2.waitKey(100)
        if k == 27 or len(position) >= 4:
            cv2.destroyAllWindows()
            break


waiting_frame = 30


def split_frames_mp4(source_file_name):
    """
    The function `split_frames_mp4` takes a video file and splits it into frames, and then displays the frames in real
    time

    :param source_file_name: the name of the video file, which is the name of the video file without the suffix
    """

    temperature = np.zeros((19, 19), dtype=int)

    for i in range(0, 19):
        for j in range(0, 19):
            temperature[i, j] = waiting_frame

    video_path = os.path.join('./assets/', source_file_name + '.mp4')
    times = 0

    # get the image from the video, with the frequency of frame_frequency
    frame_frequency = 1

    camera = cv2.VideoCapture(video_path)
    # frame_width = int(camera.get(cv2.CAP_PROP_FRAME_WIDTH))
    # frame_height = int(camera.get(cv2.CAP_PROP_FRAME_HEIGHT))

    # initialize the variable
    lines = []
    line_count = 0
    feature = 255 * 3 // 2

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(('localhost', 1111))

    print('Client start!')

    #     initialize_position(camera)

    set_args = False
    if len(position) >= 4:
        warp_args = initialize_transform()
        set_args = True

    while True:
        times += 1
        res, image = camera.read()

        if set_args:
            image = cv2.warpPerspective(image, warp_args, (width, height))

        if not res:
            # print('not res , not image')
            break

        if line_count != 38:
            line_count, lines = get_chess_line(image)
            feature = checkPos.get_board_avg_color(image, lines)

        if times % frame_frequency == 0:
            # cv2.imwrite(outPutDirName + str(times)+'.jpg', image)
            go_map, image = checkPos.trans_go_map(image, lines, feature, temperature, sock)
            # time_text = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            # cv2.putText(image, time_text, (word_x,word_y),
            # cv2.FONT_HERSHEY_SIMPLEX,1,(55,255,155),2)
            cv2.imshow("real_time", image)
            # print(outPutDirName + str(times)+'.jpg')

        # cv2.imwrite(outPutDirName + str(times) + '.jpg', image)
        # print(times)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    camera.release()
    sock.close()
    print('Client end!')


if __name__ == '__main__':
    im_file = "./assets/"

    for im_name in os.listdir(im_file):
        suffix_file = os.path.splitext(im_name)[-1]
        if suffix_file == '.mp4':
            sourceFileName = os.path.splitext(im_name)[0]
            split_frames_mp4(sourceFileName)
