/*
****************************************************************************
BSD License
Copyright (c) 2014, i-Vizon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. All advertising materials mentioning features or use of this software
   must display the following acknowledgement:
   This product includes software developed by the i-Vizon.
4. Neither the name of the i-Vizon nor the
   names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Mageswaran.D ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Mageswaran.D BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

****************************************************************************
*/
/*
* =============================================================================
*
*
*   FILENAME            : TEMPLATE.cpp
*
*   DESCRIPTION         : 
*
*   AUTHOR              : Mageswaran D
*
*
*   CHANGE HISTORY      :
*
*   DATE                : 04th May 2014
*
*   File Created.
*
* =============================================================================
*/
#include <cl_wrapper.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

int main()
{
    std::cout << "Learning adding Two Images in OpenCL" << std::endl;

    //Assuming both the image sizes are same
    cv::Mat pngFile1;
    cv::Mat pngFile2;
    int width, height, step;

    pngFile1    =   cv::imread("nature.jpeg");
    pngFile2    =   cv::imread("shiva2.jpg");

    cv::cvtColor(pngFile1, pngFile1, CV_BGR2BGRA);
    cv::cvtColor(pngFile2, pngFile2, CV_BGR2BGRA);

    if(pngFile1.cols != pngFile2.cols && pngFile1.rows != pngFile2.rows)
        ERROR_PRINT_STRING("Please choose images of same size");

    width   =   pngFile1.size().width;
    height  =   pngFile1.size().height;
    step    =   pngFile1.step;

    DEBUG_VALUE("Image Width    : ", width);
    DEBUG_VALUE("Image Height   : ", height);
    DEBUG_VALUE("Image Step     : ", step);
    DEBUG_VALUE("Image Type     : ", getImgType(pngFile1.type()));
    //std::string getImgType(int imgTypeInt)

    cv::imshow("1.PNG", pngFile1);
    cv::imshow("2.PNG", pngFile2);
    cv::waitKey();

    iv::CLSetup         cl;
    iv::Program*        prog;
    iv::KernelLauncher* kl;
    iv::Sampler*        sampler;
    std::string         file;
    std::vector<std::string> kernelFiles;
    file = "add_image.cl";
    kernelFiles.push_back(file);

    cl.init();
    prog = cl.createProgram(kernelFiles);
    prog->buildProgram();


    cl_image_format pngFormat;
    pngFormat.image_channel_data_type = CL_UNORM_INT8;
    pngFormat.image_channel_order = CL_BGRA;

    iv::Image2D* pngBuffer1 = cl.createImage2D(width, height,
                                         &pngFormat,
                                         CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                         step,
                                         pngFile1.data);

    iv::Image2D* pngBuffer2 = cl.createImage2D(width, height,
                                         &pngFormat,
                                         CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                         step,
                                         pngFile2.data);

    iv::Image2D* pngResult = cl.createImage2D(width, height,
                                              &pngFormat,
                                              CL_MEM_WRITE_ONLY);


    kl = prog->createKernelLauncher("add_image");
    kl->pGlobal(width, height)->pLocal(4,4);
    kl->pArg(pngBuffer1->getMem())
            ->pArg(pngBuffer2->getMem())
            ->pArg(pngResult->getMem());


    kl->run();

    // Offset within the image to copy from
    size_t origin[3] = {0, 0, 0};
    // Elements to per dimension
    size_t region[3] = {pngFile1.size().width, pngFile1.size().height, 1};

    pngResult->read(pngFile1.data, origin, region);\

    cv::imshow("Result PNG", pngFile1);
    cv::waitKey();




    return 0;
}

