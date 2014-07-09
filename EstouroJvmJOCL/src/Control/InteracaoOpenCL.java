/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jocl.CL;
import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import static org.jocl.Sizeof.cl_device_id;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

/**
 *
 * @author Carlos / Andre
 */
public class InteracaoOpenCL {

    /**
     * Codigo OpenCl escrito em C99
     */
    private static String programSource
            = "__kernel void "
            + "sampleKernel(__global const float *a,"
            + "             __global const float *b,"
            + "             __global float *c)"
            + "{"
            + "    int gid = get_global_id(0);"
            + "    c[gid] = a[gid] * b[gid];"
            + "}";

    private final int interacao;
    private final long tempoInicial;
    private long tempoFinal;

    public InteracaoOpenCL(int interacao) {
        this.tempoInicial = System.currentTimeMillis();
        this.interacao = interacao;
        executaArray();

    }

    public long retornaTempo() {
        return (this.tempoFinal - this.tempoInicial);
    }

    private void executaArray() {
        int n = this.interacao;
        float srcArrayA[] = new float[n];
        float srcArrayB[] = new float[n];
        float dstArray[] = new float[n];

        // atribuir valor para as posições
        for (int i = 0; i < n; i++) {
            srcArrayA[i] = i;
            srcArrayB[i] = i;
        }

// inicio da magia 
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dst = Pointer.to(dstArray);

        //Identificando a plataforma usada por index
        final int platformIndex = 0;
        // \informando o tipo da plataforma que será usada (Cpu/Gpu)
        final long deviceType = CL.CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // vaviavel de classe que define se havera disparo de exceçoes
        CL.setExceptionsEnabled(true);

        // obtendo o numero da plataforma
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // obtendo o ID da plataforma
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtendo numero de dispositivos OPENCL por plataforma 
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtendo ID do dispositivo OPENCL
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // creando contexto do programa para o dispositivo selecionado
        cl_context context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Creando o objeto de comando OPENCL
        cl_command_queue commandQueue
                = clCreateCommandQueue(context, device, 0, null);

        // criando objeto de memoria para parametros de entrada e saida(buffer)
        cl_mem memObjects[] = new cl_mem[3];
        memObjects[0] = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * n, srcA, null);
        memObjects[1] = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * n, srcB, null);
        memObjects[2] = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_float * n, null, null);

        // criando o programa passando o codigo OPENCL
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // bildando o programa
        clBuildProgram(program, 0, null, null, null, null);

        // criando o kernel passando o nome do programa opencl
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);

        // passando os parametros do codigo opencl ponteiros das memorias alocadas
        clSetKernelArg(kernel, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, 2,
                Sizeof.cl_mem, Pointer.to(memObjects[2]));

        // setando as areas de trabalho global e local
        long global_work_size[] = new long[]{n};
        long local_work_size[] = new long[]{1};

        // Executando o Kernel (area para comando opencl,kernel com o codigo, mais areas de memoria global e local
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, local_work_size, 0, null, null);

        // lendo saida de dados
        clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
                n * Sizeof.cl_float, dst, 0, null, null);

        // liberando objetos de memoria
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);

        this.tempoFinal = System.currentTimeMillis();
        System.out.println(retornaTempo() + " milissegundos OpenCl ");
        dstArray = null;
        srcArrayA = null;
        srcArrayB = null;
        File arquivo = new File("c:\\AppsPrjFinal\\ESTOUROJVMJOCL.txt");
        try (FileWriter fw = new FileWriter(arquivo)) {
            fw.write(Long.toString(retornaTempo()));
            fw.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
