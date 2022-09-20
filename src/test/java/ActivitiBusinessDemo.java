import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class ActivitiBusinessDemo {

    /*
    添加业务key到Activiti表中（启动流程时，写入流程实例表）
     */
    @Test
    public void addBusinessKey() {
        //获取流程引擎、
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //启动流程过程中，添加businesskey
        //参数：流程对应key，businesskey，存出差申请单的id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection", "1001");
        //输出
        System.out.println("businesskey=" + processInstance.getBusinessKey());
    }

    /*
    全部流程实例的挂起和激活
    suspend 暂停
     */
    @Test
    public void suspendAllProcessInstance() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //查询流程定义信息
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .singleResult();
        //获取当前流程定义的实例是否都是挂起状态
        boolean suspended = definition.isSuspended();
        //获取流程定义id
        String definitionId = definition.getId();
        //如果是挂起状态，改为激活
        //参数:流程定义id，是否激活，激活时间
        if (suspended) {
            repositoryService.activateProcessDefinitionById(definitionId, true, null);
            System.out.println("流程定义id="+definitionId+"已激活");
        } else {
            //参数:流程定义id，是否暂停，暂停时间
            //如果是激活状态，改为挂起状态
            repositoryService.suspendProcessDefinitionById(definitionId, true, null);
            System.out.println("流程定义id="+definitionId+"已挂起");
        }
    }

    /*
    挂起或激活单个流程实例
     */
    @Test
    public void suspendSingleInstance(){
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //通过RuntimeService获取流程实例对象
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("27501")
                .singleResult();
        //得到当前流程实例的暂停状态
        boolean suspended = instance.isSuspended();
        //获取流程实例id
        String instanceId = instance.getId();
        //判断
        if (suspended){
            runtimeService.activateProcessInstanceById(instanceId);
            System.out.println("流程实例状态为"+instance.isSuspended());
            System.out.println("流程实例id="+instanceId+"已激活");
        }else {
            runtimeService.suspendProcessInstanceById(instanceId);
            System.out.println("流程实例状态为"+instance.isSuspended());
            System.out.println("流程实例id="+instanceId+"已挂起");
        }
    }

    /*
    测试完成个人任务
     */
    @Test
    public void completeTask(){
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取taskService
        TaskService taskService = processEngine.getTaskService();
        //使用taskService获取任务，通过实例id，负责人
        Task task = taskService.createTaskQuery()
                .processInstanceId("27501")
                .taskAssignee("zhangsan")
                .singleResult();
        System.out.println("流程实例id="+task.getProcessInstanceId());
        System.out.println("流程任务id="+task.getId());
        System.out.println("负责人="+task.getAssignee());
        System.out.println("任务名称="+task.getName());
        //根据任务id完成任务
        taskService.complete(task.getId());
    }
}
