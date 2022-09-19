import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

public class ActivitiDemo {

    //部署流程
    @Test
    public void testDeployment(){
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
        System.out.println("流程部署id="+deployment.getId());
        System.out.println("流程部署名称="+deployment.getName());

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
    public void testStartProcess(){
        //创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //根据流程定义的id来启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection");
        //输出
        System.out.println("流程定义id="+processInstance.getProcessDefinitionId());
        System.out.println("流程实例id="+processInstance.getId());
        System.out.println("当前活动的ID="+processInstance.getActivityId());
    }

    /*
      查询个人待执行的任务
    */
    @Test
    public void testFindPersonTaskList(){
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
            System.out.println("流程实例id="+task.getProcessInstanceId());
            System.out.println("任务id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
            System.out.println("任务名称="+task.getName());
        }
    }

    /*
    完成个人任务
     */
    @Test
    public void complete(){
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取taskService
        TaskService taskService = processEngine.getTaskService();
        //根据任务id完成任务
        taskService.complete("2505");
    }
}
