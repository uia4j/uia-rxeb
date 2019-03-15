ReactiveX Event Bus
===
## 動機


## 概念
### 事件樹
事件樹內包含節點（node）和 標記（tag）：
* 節點用來建立階層關係
* 標記用來定義節點的屬性

### 調度
當標記（tag）的值發生變化時，會產生變更事件（onUpdate）通知監聽者。不同於以往，監聽者除了直接追蹤標記（tag）也可追蹤節點（node）。當追蹤對象為節點時，節點下的任一屬性發生變化後，監聽者都會收通知。


## 範例

```java
/**
 * Event Tree, naming pattern is {path}.{NAME}, case sensitive.
 * fab
 *   group1 (NAME)
 *     e1 (CO2, NO2)
 *     e2 (CO, CO2, NO2)
 *   group2 (NAME)
 *
 */
EventTree tree = new EventTree();

EventNode fab1 = tree.createNode("fab");

EventNode group1 = fab1.addNode("group1");
group1.addTag("NAME");
EventNode e1 = group1.addNode("e1");
e1.addTag("NO2");
e1.addTag("CO2");
EventNode e2 = group1.addNode("e2");
e2.addTag("NO2");
e2.addTag("CO");
e2.addTag("CO2");

EventNode group2 = fab1.addNode("group2");
group2.addTag("NAME");

EventMediator mediator = tree.createMediator();

// observe the changes on the path 'fab' and sub-paths.
med.register("fab", new EventNodeObserverImpl("obv1"), true);

// observe the changes on the path 'fab.group1' itself.
med.register("fab.group1", new EventNodeObserverImpl("obv2"), false);

// observe the changes on the path 'fab.group1' and sub-paths.
med.register("fab.group1", new EventNodeObserverImpl("obv3"), true);

// observe the changes of the tag 'fab.group1.e1.CO2'.
med.register("fab.group1.e1.CO2", new EventNodeObserverImpl("obv4"));


// emit last values to fab.group.e1.CO2.
// obv1, obv3 and obv4 will receive onUpdate() signal.
med.emit("fab.group1.e1.CO2", "12.3");


// emit last values to fab.group2.NAME and fab.group.e1.NO2.
// obv1 and obv3 will receive onUpdate() signal.
TreeMap<String, Object> dataset = new TreeMap<String, Object>();
dataset.put("fab.group2.NAME", "Group2");
dataset.put("fab.group1.e1.NO2", "0.4");
med.emit(dataset);


```
