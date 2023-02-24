import cv2
import time
from openvino.inference_engine import IECore
from datetime import datetime
import os
import argparse

parser = argparse.ArgumentParser(description='Runs AI inference on a Myriad device for a set of tiles')
parser.add_argument('-x','--model_xml', help='The model xml file path', required=True)
parser.add_argument('-b','--model_bin', help='The model bin file path', required=True)
parser.add_argument('-i','--input_tiles', help='The input tiles directory', required=True)
parser.add_argument('-o','--output_tiles', help='The ouput tiles directory', required=True)
args = vars(parser.parse_args())

# Model IR files
model_xml = args['model_xml']
model_bin = args['model_bin']

# Input image tiles path
input_tiles_path = args['input_tiles']

# Output image tiles path
output_folder = args['output_tiles']
os.makedirs(output_folder, exist_ok=True)

# Device to use
device = "MYRIAD"

print(
    "\nConfiguration parameters settings:"
    f"\n\t>> model_xml = {model_xml}",
    f"\n\t>> model_bin = {model_bin}",
    f"\n\t>> input_tiles_path = {input_tiles_path}",
    f"\n\t>> device = {device}", 
)

# Create an Inference Engine instance
print("\nCreating Inference Engine...")
ie = IECore()

# List the available devices
devices = ie.available_devices
print("Available devices: ", devices)

if len(devices) == 0:
    print("There are no available devices! Make sure the MYRIAD device is plugged correctly!")
    exit(1)

# Read the network from IR files
print("\nReading the network...")
net_read_time = time.time()
net = ie.read_network(model=model_xml, weights=model_bin) 
net_read_time = time.time() - net_read_time
print(f"  >> The network was read in: {net_read_time * 1000:.1f} ms")

print("Loading the network to the device...")
net_load_time = time.time()
exec_net = ie.load_network(network=net, num_requests=2, device_name=device)
net_load_time = time.time() - net_load_time
print(f"  >> The network was loaded to the device in: {net_load_time * 1000:.1f} ms")

# Store names of input and output blobs
input_blob = next(iter(net.inputs))
output_blob = next(iter(net.outputs))

# Read the input dimensions: n=batch size, c=number of channels, h=height, w=width
n, c, h, w = net.inputs[input_blob].shape

print(f"Loaded the model into the Inference Engine for the {device} device.", 
      f"\nModel input dimensions: n={n}, c={c}, h={h}, w={w}")

# Define the function to load the input image
def load_input_image(input_path):
    print("Loading input image...")
    # Globals to store input width and height
    global input_w, input_h
    
    # Use OpenCV to load the input image
    img = cv2.imread(input_path)
    
    input_h, input_w, *_ = img.shape
    print("  >> Loaded the input image: %s \n  >> Input image resolution: %s x %s" % (input_path,input_w,input_h))
    return img

# Define the function to resize the input image
def resize_input_image(image):
    print("Resizing input image...")
    # Resize the image dimensions from image to model input w x h
    in_frame = cv2.resize(image, (w, h))
    # Change data layout from HWC to CHW
    in_frame = in_frame.transpose((2, 0, 1))  
    # Reshape to input dimensions
    in_frame = in_frame.reshape((n, c, h, w))
    #print(f"Resized the input image to {w}x{h}.")
    print("  >> Resized the input image to: %s x %s." % (w,h))
    return in_frame


from pathlib import Path
from PIL import Image

pathlist = Path(input_tiles_path).glob('*')
for path in pathlist:
    print("-------------")
    # Convert path from object to string & Load the image
    path_in_str = str(path)
    image = load_input_image(path_in_str)

    # Resize the input image
    in_frame = resize_input_image(image)

    # Run the inference
    print("Running inference...")
    inf_start = time.time()
    res = exec_net.infer(inputs={input_blob: in_frame})   
    inf_time = time.time() - inf_start
    print(f"  >> Inference is complete. Run time: {inf_time * 1000:.1f} ms")
    #print('  >> Model output_blob has the following shape: %s'  % ({res[output_blob].shape}))

    image_outputed = list(res.values())[0]
    last = cv2.resize(image, (image_outputed.shape[2], image_outputed.shape[3]))
    #print(image_outputed.shape)
    #print(last.shape)

    print("Storing image...")
    img = Image.fromarray(last, 'RGB')
    filename = os.path.basename(path_in_str)
    img.save(output_folder + '/' + filename)
    print("  >> Stored!")

print("\nThe python script has finished all tasks... Closing now!")
