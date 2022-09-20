import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

public class TestListener {
    @Test
    public void testDeployment() {
        //创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //使用RepositoryService进行流程部署，定义一个流程名字，把bpmn和png部署到数据库中
        Deployment deployment = repositoryService.createDeployment().name("监听器")
                .addClasspathResource("bpmn/listener.bpmn")
                .deploy();
        //输出
        System.out.println("流程部署id=" + deployment.getId());
        System.out.println("流程部署名称=" + deployment.getName());
    }


    @Test
    public void startAssigneeUel() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取runtimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //启动流程实例
        runtimeService.startProcessInstanceByKey("myListener");
    }
}
