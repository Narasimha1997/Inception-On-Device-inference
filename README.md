**OnDevice Tensorflow Machine Learning Experiment**
----------
*I developed this android app as a part of learning Tensorflow on-device machine learning.* 
Features :

 - Image recognition using Inception v3 (GoogleNet)
 - Works offline, no internet or powerful backend required.
 - Real time, with minimum delay, takes 1.3 seconds on 8 core processor.
 
 ## Preparing the model to run on android device :
 Android is a platform with minimum computation capacity when compared to desktops or workstations, this poses a challenge for running complex models, But recently Google's tensorflow community released a tool called **Graph Transform Tool**. This tool can be used to optimize tensorflow graph to run smoothly on Android devices.
  Refer to the documentation of Graph Transform tool here : 
  [Graph Transform tool](https://github.com/tensorflow/tensorflow/blob/master/tensorflow/tools/graph_transforms/README.md)
Steps : 
 
 - Compile graph transform tool as explained in the documentation,
 - Apply fold convolutions and round - weights, do not apply quantize-weights as it can corrupt the graph (Sometimes).
 - Remove JpegDecoder operation as it is a no-op on Android as of now ***JpegDecoder is not supported as it a op used only during training and thus it is not required during inference****,, you need to build your own JPEG decoder which converts Image (Bitmap on Android) into 3 dimensional tensor. 

Once you perform above operations, you get a inception.pb frozen protocol buffer file which is optimized, but there is no change in the model file size.
But the model becomes **compressible**, 95.6 MB of model can be compressed to 26.5 MB because of round-weights technique, Files are automatically compressed during packaging phase of Android Studio build process.

**

## Processing labels  using java.collection.HashMap:
There are 2 files in the project which represents various classes which can be recognised by Inception v3, labelmap.pbtxt and labels.txt are the 2 files which needs to loaded into memory during inference to obtain Class Label (Human readable label) from the softmax activation at output layer (softmax:0). 
Steps: 

 - Place labelmap.pbtxt and labels.txt into assets directory.
 - Implement file reader functions to read both the files.
 - Use string processing functions to convert strings (key, value) pairs.
 - The key value pairs should be of the form :   ***<argmax, uid_label>*** for labelmap.txt and ***<uid_label, human_readable_label>*** for label.txt file.
 - Join the maps to obtain Map of the form ***<argmax, human_redable_label>*** ,  argmax is integer which represents a neuron which has fired with higher probability.
 - We do this only once, i.e during initialisation of application, so we can save time later as labels are already processed, so create a static class which can hold the final Map object permanently.
 
 ## Loading the model and running inference:   

Copy optimised inception graph to assets directory, Load the graph by using ***TensorFlowInferenceInterface*** , create an output tensor which is of shape (1, 1000) i.e it is a list of all output neurons, since inception uses softmax as output activation functions, the output array contains probability distribution of all classes. 
Steps :

 - Build a JepgDecoder function, which converts Image to array.
 - Use camera api to create UI used for capturing images.
 - Obtain bitmap object and then process it using JpegDecoder you have impelemented.
 - Tensorflow session has to be created only once to save time, feed the image array to input of neural network ,use **Mul:0** as input layer.
 - Create an array which copies output and obtain maximum value from it.
 - Use Map object which was processed during application creation to obtain ***human_readable_label***  from **argmax** .
 - Display the output.
 

