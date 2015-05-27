//package com.supun.pattern;
public class SquarePattern {
 
/**
 * @param args
 */
 
 //initialize inputs
 //static int x1,x2,x3,x4;
 static double[][] input = {{0,1,1,0}, {1,0,0,1}, {1,1,0,0}};
 static int[][] target = {{0,1}, {1,0}, {1,1}};
 
 //initialize weights
 static double w1,w2,w3,w4,w5,w6,w7,w8,w9,w10,w11,w12,w13,w14,w15,w16,w17,w18;
 
 //initialize output
 // y4 and y5 are the final outputs
 static double y1,y2,y3,y4,y5;
 
 //initializing errors
 static double delta1,delta2,delta3,delta4,delta5;
 //initializing bias
 static double b1,b2,b3,b4,b5;
 
 //learning rate
 static double learningRate=0.4;
 static int count = 0;
 static int maxCount = 500;
 static boolean loop = true;
 public static void main(String[] args) {
 w1 =0.4;
 w2 =0.4;
 w3 =0.4;
 w4 =0.7;
 w5 =0.4;
 w6 =0.4;
 w7 =0.6;
 w8 =0.4;
 w9 =0.4;
 w10 =0.4;
 w11 =0.4;
 w12 =0.4;
 w13 =0.4;
 w14 =0.4;
 w15 =0.4;
 w16 =0.4;
 w17 =0.4;
 w18 =0.4;
 
 b1 = 0.2;
 b2 = 0.5;
 b3 = 0.6;
 b4 = 0.2;
 b5 = 0.3;
 
 //System.out.println(b5);
 while(loop){
 
 for(int i=0;i<input.length;i++){
 calculateY(input[i][0], input[i][1],input[i][2],input[i][3]);
 calculateDelta(i);
 calculateNewWeights(i);
 calculateNewBias();
 count++;
 
 System.out.println(y4 + ", "+ y5);
 }
 
 if(count>=maxCount){
 loop = false;
 }
 }
 
}
 static void calculateY(double x1,double x2, double x3,double x4){
 y1 = sigmoid((x1*w1)+(x2*w4)+(x3*w7)+(x4*w10)+b1);
 y2 = sigmoid((x1*w2)+(x2*w5)+(x3*w8)+(x4*w11)+b2);
 y3 = sigmoid((x1*w3)+(x2*w6)+(x3*w9)+(x4*w12)+b3);
 y4 = sigmoid((y1*w13)+(y2*w15)+(y3*w17)+b4);
 y5 = sigmoid((y1*w14)+(y2*w16)+(y3*w18)+b5);
 //return y4;
 }
 
static void calculateDelta(int j){
 delta5 = target[j][1] - y5;
 delta4 = target[j][0] - y4;
 delta3 = (delta4*w17) + delta5*(w18);
 delta2 = (delta4*w15) + delta5*(w16);
 delta1 = (delta4*w13) + delta5*(w14);
 }
 
 private static void calculateNewWeights(int j){
 w1 += (learningRate*delta1*input[j][0]*y1*(1-y1));
 w4 += (learningRate*delta1*input[j][1]*y1*(1-y1));
 w7 += (learningRate*delta1*input[j][2]*y1*(1-y1));
 w10 += (learningRate*delta1*input[j][3]*y1*(1-y1));
 
 w2 += (learningRate*delta2*input[j][0]*y2*(1-y2));
 w5 += (learningRate*delta2*input[j][1]*y2*(1-y2));
 w8 += (learningRate*delta2*input[j][2]*y2*(1-y2));
 w11 += (learningRate*delta2*input[j][3]*y2*(1-y2));
 
 w3 += (learningRate*delta3*input[j][0]*y3*(1-y3));
 w6 += (learningRate*delta3*input[j][1]*y3*(1-y3));
 w9 += (learningRate*delta3*input[j][2]*y3*(1-y3));
 w12 += (learningRate*delta3*input[j][3]*y3*(1-y3));
 
 w13 += (learningRate*delta4*y1*y4*(1-y4));
 w15 += (learningRate*delta4*y2*y4*(1-y4));
 w17 += (learningRate*delta4*y3*y4*(1-y4));
 
 w14 += (learningRate*delta5*y1*y5*(1-y5));
 w16 += (learningRate*delta5*y2*y5*(1-y5));
 w18 += (learningRate*delta5*y3*y5*(1-y5));
 
}
 private static void calculateNewBias(){
 b1 += delta1;
 b2 +=delta2;
 b3 +=delta3;
 b4 +=delta4;
 b5 +=delta5;
 }
 static double sigmoid(double exponent){
 return (1.0/(1+Math.pow(Math.E,(-1)*exponent)));
 }
}
