import cv2
import numpy as np

image = cv2.imread("asserts/chess7.png")
# 图像翻转
# imgflip = cv2.flip(image, 0)

size = image.shape

for i in range(size[0]):
    for j in range(size[1]):
        rgb_min = 255
        rgb_sum = 0
        for k in image[i, j]:
            rgb_min = min(rgb_min, k)
            rgb_sum += k
        if abs(rgb_min * 3 - rgb_sum) > 5.0 * rgb_min:
            image[i, j] = [255, 255, 255]
# 252, 230, 173
# 162, 124, 115

# 87, 68, 32
# 120, 72, 53


# 图像复制
# img_copy = image.copy()

# 灰度图像转为彩色图像
# img3 = cv2.cvtColor(img_copy, cv2.COLOR_GRAY2RGB)
# 彩色图像转为灰度图像
# img2 = cv2.cvtColor(img3, cv2.COLOR_RGB2GRAY)

cv2.imshow('image', image)
cv2.waitKey(0)  # 等待输入任何按键，当用户输入任何一个按键后即调用destroyAllWindows()关闭所有图像窗口
cv2.destroyAllWindows()  # 摧毁窗口
