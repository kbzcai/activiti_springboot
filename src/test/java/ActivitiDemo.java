import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

public class ActivitiDemo {

    //部署流程
    /*
     act_ge_bytearray
     act_re_deployment
     act_re_procdef
     */
    @Test
    public void testDeployment() {
        //创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //使用RepositoryService进行流程部署，定义一个流程名字，把bpmn和png部署到数据库中
        Deployment deployment = repositoryService.createDeployment().name("出差申请流程")
                .addClasspathResource("bpmn/evection.bpmn")
                .addClasspathResource("bpmn/evection.png")
                .deploy();
        //输出
        System.out.println("流程部署id=" + deployment.getId());
        System.out.println("流程部署名称=" + deployment.getName());

    }


    //启动流程实例
    /*
     * act_hi_actinst         流程实例执行历史
     * act_hi_identitylink    流程的参与用户信息历史
     * act_hi_procinst        流程实例历史信息
     * act_hi_taskinst        流程任务历史信息
     * act_ru_execution       流程正在执行信息
     * act_ru_identitylink    流程的参与用户信息
     * act_ru_task            任务信息
     * */
    @Test
    public void testStartProcess() {
        //创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //根据流程定义的id来启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection");
        //输出
        System.out.println("流程定义id=" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id=" + processInstance.getId());
        System.out.println("当前活动的ID=" + processInstance.getActivityId());
    }

    /*
      查询个人待执行的任务
    */
    @Test
    public void testFindPersonTaskList() {
        //创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //根据流程的key和任务的负责人查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("myEvection")//流程key
                .taskAssignee("zhangsan")//负责人
                .list();
        //输出
        for (Task task : list) {
            System.out.println("流程实例id=" + task.getProcessInstanceId());
            System.out.println("任务id=" + task.getId());
            System.out.println("任务负责人=" + task.getAssignee());
            System.out.println("任务名称=" + task.getName());
        }
    }

    /*
    完成个人任务
     */
    @Test
    public void completeTask() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取taskService
        TaskService taskService = processEngine.getTaskService();
        //根据任务id完成任务,完成zhangsan任务
        //taskService.complete("2505");
        //获取jerry - myEvection 对应的任务   对应前端完成任务时，获取该流程的key和目前的负责人，传到后端进行操作
//        Task task = taskService.createTaskQuery().processDefinitionKey("myEvection")
//                .taskAssignee("joker")
//                .singleResult();
        //完成jack的任务
//        Task task = taskService.createTaskQuery().processDefinitionKey("myEvection")
//                .taskAssignee("jack")
//                .singleResult();
        //完成rose的任务
        Task task = taskService.createTaskQuery().processDefinitionKey("myEvection")
                .taskAssignee("rose")
                .singleResult();
        System.out.println("流程实例id=" + task.getProcessInstanceId());
        System.out.println("任务id=" + task.getId());
        System.out.println("任务负责人=" + task.getAssignee());
        System.out.println("任务名称=" + task.getName());
        //完成jerry的任务
        taskService.complete(task.getId());

    }

    //使用zip包进行批量部署
    @Test
    public void deployProcessByZip() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //流程部署
        //读取资源包文件，构造成inputStream
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("bpmn/evection.zip");
        //用inputStream构造ZipInputStream
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //使用压缩包的流进行流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程部署id=" + deploy.getId());
        System.out.println("流程部署名称name=" + deploy.getName());
    }


    /*
    查询流程定义
    */
    @Test
    public void queryProcessDefinition() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //获取ProcessDifinitionQuery对象
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        //查询当前所有流程定义的集合 根据key查询，并根据版本号倒序进行排序
        List<ProcessDefinition> definitionList = definitionQuery.processDefinitionKey("myEvection")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        //输出
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义id=" + processDefinition.getId());
            System.out.println("流程定义名称=" + processDefinition.getName());
            System.out.println("流程定义key=" + processDefinition.getKey());
            System.out.println("流程定义版本=" + processDefinition.getVersion());
            System.out.println("流程部署ID=" + processDefinition.getDeploymentId());
        }
    }

    /*
    删除流程部署信息
     act_ge_bytearray
     act_re_deployment
     act_re_procdef
     当前流程如果并没有完成，要删除需要使用特殊方式，原理就是级联删除
     */
    @Test
    public void deleteDeployment() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //通过部署id来删除流程部署信息
        String deploymentId = "70001";
//        repositoryService.deleteDeployment(deploymentId);
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /*
    下载资源文件
    方案1:使用Activiti提供的api来下载文件，保存到文件目录还是要用io
    方案2:自己写代码从数据库中下载，使用jdbc对blob类型或者clob类型数据进行读取，保存到文件目录
    解决io操作:commons-io.jar
    一般使用api下载 RepositoryService
     */
    @Test
    public void getDeploymentFile() throws IOException {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //获取查询对象
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition processDefinition = definitionQuery.processDefinitionKey("myEvection")
                .singleResult();
        //通过流程定义信息，获取部署id
        String deploymentId = processDefinition.getDeploymentId();
        //传递部署id，读取资源信息(png,bpmn)
        //获取png流 从流程定义表中获取png的目录和名字
        String diagramResourceName = processDefinition.getDiagramResourceName();
        InputStream pngInputStream = repositoryService.getResourceAsStream(deploymentId, diagramResourceName);
        //获取bpmn的流
        String resourceName = processDefinition.getResourceName();
        InputStream bpmnInputStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
        //构造OutPutStream流
        File pngFile = new File("d:/evectionflow01.png");
        File bpmnFile = new File("d:/evectionflow01.bpmn");
        FileOutputStream pngFileOutputStream = new FileOutputStream(pngFile);
        FileOutputStream bpmnFileOutputStream = new FileOutputStream(bpmnFile);
        //输入流、输出流的转换
        IOUtils.copy(pngInputStream, pngFileOutputStream);
        IOUtils.copy(bpmnInputStream, bpmnFileOutputStream);
        //关闭流
        pngFileOutputStream.close();
        bpmnFileOutputStream.close();
        pngInputStream.close();
        bpmnInputStream.close();
    }

    /*
    流程历史信息查看
     */
    @Test
    public void findHistoryInfo(){
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取HistoryService
        HistoryService historyService = processEngine.getHistoryService();
        //查询 actinst表
        //获取actinst表的查询对象 根据instanceid查询
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        historicActivityInstanceQuery.processInstanceId("12501");
        //根据definitionid
//        historicActivityInstanceQuery.processDefinitionId("myEvection:1:10004");
        //增加排序操作 根据开始时间升序
        historicActivityInstanceQuery.orderByHistoricActivityInstanceStartTime().asc();
        //查询所有内容
        List<HistoricActivityInstance> list = historicActivityInstanceQuery.list();
        //输出
        for (HistoricActivityInstance hi : list) {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("======================================");
        }
    }
}
