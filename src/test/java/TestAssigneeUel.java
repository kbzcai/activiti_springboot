import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestAssigneeUel {
    @Test
    public void testDeployment() {
        //创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //使用RepositoryService进行流程部署，定义一个流程名字，把bpmn和png部署到数据库中
        Deployment deployment = repositoryService.createDeployment().name("出差申请流程-uel")
                .addClasspathResource("bpmn/evection-uel.bpmn")
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
        //设定assignee的值，用来替换uel表达式
        Map<String, Object> map = new HashMap<>();
        map.put("assignee0","张三");
        map.put("assignee1","李经理");
        map.put("assignee2","王总经理");
        map.put("assignee3","赵财务");
        runtimeService.startProcessInstanceByKey("myEvection1", map);
    }
}
