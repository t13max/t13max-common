package com.t13max.ai.behavior4j.data;

import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.BehaviorTree;
import com.t13max.ai.behavior4j.plugins.ScriptDataSource;
import groovy.lang.GroovyObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
@Log4j2
public class BehaviorTreeManager {

    private static volatile BehaviorTreeManager instance;

    @Getter
    @Setter
    private String path;

    private final Map<String, Class<?>> btAgents = new HashMap<>();

    protected BehaviorTreeRepository repository;

    private ScriptDataSource dataSource;

    private BehaviorTreeManager() {
        repository = new BehaviorTreeRepository();
    }

    public void bindDataSource(ScriptDataSource dataSource, String BTPath) {
        this.dataSource = dataSource;
        this.path = BTPath;
        if (log.isInfoEnabled()) {
            log.info("Bind behavior path : " + BTPath);
        }
    }

    public static BehaviorTreeManager getInstance() {
        BehaviorTreeManager manager = instance;
        if (manager == null) {
            synchronized (BehaviorTreeManager.class) {
                manager = instance;
                if (manager == null) {
                    instance = manager = new BehaviorTreeManager();
                }
            }
        }

        return manager;
    }

    public ScriptDefine newScript(String name) {
        ScriptDefine script = new ScriptDefine();
        script.setPath(name);

        return script;
    }

    public GroovyObject loadGroovy(String path) {
        return dataSource.loadScript(path);
    }

    public ScriptDefine loadScript(String name) {
        ScriptDefine script = new ScriptDefine(dataSource.loadScript(name));
        script.setPath(name);

        return script;
    }

    public void loadBehaviorTree(File treeFile) {
        try {
            loadTree2Repository(treeFile, this.repository);
        } catch (Exception e) {
            log.error("加载行为树 {} 失败！！！", treeFile.getName());
            e.printStackTrace();
        }
    }

    public <T> BehaviorTree<T> createBehaviorTree(String treeName) {
        BehaviorTree<T> behaviorTree = repository.getBehaviorTree(treeName);
        if (behaviorTree == null) {
            behaviorTree = loadTree2Repository(new File(path + treeName + ".xml"), this.repository);
        }

        return behaviorTree.newInstance();
    }

    public void reloadBehaviorTrees() {
        Set<String> treeNames = new HashSet<>(repository.getRepository().keySet());
        BehaviorTreeRepository newRepository = new BehaviorTreeRepository(treeNames.size());
        treeNames.forEach(name -> {
            File file = new File(path + name + ".xml");
            if (!file.exists())
                return;
            loadTree2Repository(file, newRepository);
        });
        this.repository = newRepository;
        if (log.isInfoEnabled()) {
            log.info("Behavior Tree Reload Complete！");
        }
    }

//    public void reloadBehaviorTrees(String[] treeNames) {
//        for (String name : treeNames) {
//            repository.removeTrees(name);
//        }
//        for (String name : treeNames) {
//            File file = new File(path + name + ".xml");
//            if (!file.exists())
//                return;
//            loadTree2Repository(file, this.repository);
//        }
//        if (log.isInfoEnabled()) {
//            log.info("Behavior Tree Reload Complete！");
//        }
//    }

    public <T> BTNode<T> createRootNode(String treeName) {
        BehaviorTree<T> behaviorTree = createBehaviorTree(treeName);

        return behaviorTree.getChild(0);
    }

    public Class<?> getBTType(String btName) {
        if (this.btAgents.isEmpty()) {
            return null;
        }

        return this.btAgents.get(btName);
    }

    private <T> BehaviorTree<T> loadTree2Repository(File file, BehaviorTreeRepository repository) {
        if (file == null || file.isDirectory())
            throw new IllegalArgumentException();

        BehaviorTree<T> behaviorTree = new BehaviorTree<>();
        String path = BehaviorTreeManager.getInstance().getPath();

        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element treeElement = document.getRootElement();
            String btName = treeElement.attributeValue("name");
            if (repository.contain(btName))
                return repository.getBehaviorTree(btName);

            String agentType = treeElement.attributeValue("importClass");
            if (agentType != null) {
                try {
                    Class<?> clazz = Class.forName(agentType);
                    btAgents.put(btName, clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            behaviorTree.setName(btName);
            ScriptDefine script = BehaviorTreeManager.getInstance().newScript(path + btName + ".groovy");
            List<BTNode<T>> rootNodes = BehaviorTreeLoader.loadElement(treeElement, script);
            BTNode<T> rootNode = rootNodes.size() > 0 ? rootNodes.get(0) : null;
            behaviorTree.setRootNode(rootNode);

            String traceInfo = treeElement.attributeValue("trace");
            behaviorTree.setTraceInfo(traceInfo);

            repository.registerTree(behaviorTree.getName(), behaviorTree);
            if (log.isInfoEnabled()) {
                log.info("Load behavior tree : {}",behaviorTree.getName());
            }
        } catch (DocumentException e) {

            e.printStackTrace();
        }

        return behaviorTree;
    }
}
