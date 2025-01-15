# 读表打表模块

### 支持的数据类型

- int float String
- array<int> map<int,int>
- 未来支持 多元组 其他类型的array和map

### 备忘录

- 后续增加md5校验 不重复打表
- 多线程打表
- 生成打表jar包合运行脚本 方便使用
- 丰富数据类型


VM参数
-cp /Users/antingbi/IdeaProjects/t13max-card-match-maker/static-lib/* com.t13max.template.gen.TempGenerator
环境变量
excelPath=/Users/antingbi/IdeaProjects/t13max-card-match-maker/static-template;javaPath=/Users/antingbi/IdeaProjects/t13max-card-match-maker/common-template/src/main/java/com/t13max/template/temp;jsonPath=/Users/antingbi/IdeaProjects/t13max-card-match-maker/static-json/json;packageName=com.t13max.template.temp
