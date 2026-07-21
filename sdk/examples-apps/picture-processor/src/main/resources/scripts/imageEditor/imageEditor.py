from PIL import Image
from PIL import ImageFont
from PIL import ImageDraw 

from sys import argv
from datetime import datetime
from os import path, environ
from time import sleep


if environ['ENV_PROCESS_DURATION']:
    duration = int(environ['ENV_PROCESS_DURATION'])
    print("process duration in seconds: ", duration)
    for i in range(duration):
        print("processing ", i)
        sleep(1)

imagePath = argv[1]
print("processing image ", imagePath )
img = Image.open(imagePath)

draw = ImageDraw.Draw(img)
font = ImageFont.truetype("Inconsolata-Regular.ttf", 120)

timestamp = datetime.utcnow().strftime('%Y-%m-%d-%H-%M-%S-%f')
draw.text((0, 0),timestamp,(255,255,255),font=font)

imageDir, imageFile = path.split(imagePath)
modImageFile = timestamp + imageFile
modImagePath = path.join(imageDir, modImageFile)
print("modified image ", modImagePath )
img.save(modImagePath)



