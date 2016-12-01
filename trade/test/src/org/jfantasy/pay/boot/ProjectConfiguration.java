package org.jfantasy.pay.boot;


import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

//@Component
public class ProjectConfiguration implements CommandLineRunner {

    @Autowired
    private ProjectService projectService;

    @Override
    public void run(String... args) throws Exception {
        Project project = projectService.get("test");
        if(project == null){
            project = new Project();
            project.setKey("test");
            project.setName("测试订单");
            projectService.save(project);
        }
    }

}
