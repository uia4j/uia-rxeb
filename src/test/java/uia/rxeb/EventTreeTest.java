package uia.rxeb;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

public class EventTreeTest {

    @Test
    public void testTree() {
        EventTree tree = createSample();
        tree.createMediator();
        tree.println();

        Assert.assertNotNull(tree.queryNode("fab.group1.e1"));
        Assert.assertEquals(2, tree.queryTags("co2").size());
        Assert.assertEquals(0, tree.queryPathTags("fab", false).size());
        Assert.assertEquals(7, tree.queryPathTags("fab", true).size());
        Assert.assertEquals(1, tree.queryPathTags("fab.group1.NAME", true).size());
    }

    @Test
    public void testMediator1() throws InterruptedException {
        EventTree tree = createSample();

        EventMediator med = tree.createMediator();
        System.out.println();

        System.out.println("1.1 reg: fab");
        med.register("fab", new EventNodeObserverImpl("obv1"), true);

        System.out.println("1.2 reg: fab.group1");
        med.register("fab.group1", new EventNodeObserverImpl("obv2"), false);

        System.out.println("1.3 reg: fab.group1");
        med.register("fab.group1", new EventNodeObserverImpl("obv3"), true);

        System.out.println("1.4 reg: fab.group1.e1.CO2");
        med.register("fab.group1.e1.CO2", new EventNodeObserverImpl("obv4"));

        System.out.println("2.1 emit: fab.group1.e1.CO2=12.3");
        med.emit("fab.group1.e1.CO2", "12.3");

        System.out.println("2.2 emit: fab.group1.e1.NO2=11.3");
        med.emit("fab.group1.e1.NO2", "11.3");

        System.out.println("3.emit: batch");
        TreeMap<String, Object> dataset = new TreeMap<String, Object>();
        dataset.put("fab.group2.NAME", "Group2");
        dataset.put("fab.group1.e1.NO2", "0.4");
        med.emit(dataset);

        Thread.sleep(1000);
    }

    @Test
    public void testMediator2() throws InterruptedException {
        EventTree tree = createSample();
        EventMediator med = tree.createMediator();

        System.out.println();
        for (int i = 0; i < 200; i++) {
            med.register("fab", new EventNodeObserverImpl("obv" + i));
        }

        EventNodeObserverImpl.T = System.currentTimeMillis();

        TreeMap<String, Object> dataset = new TreeMap<String, Object>();
        dataset.put("fab.group2.NAME", "Group2");
        dataset.put("fab.group1.e1.NO2", "0.4");
        med.emit(dataset);

        Thread.sleep(1000);
    }

    private EventTree createSample() {
        EventTree tree = new EventTree();

        EventNode fab1 = tree.createNode("fab");

        EventNode group1 = fab1.addNode("group1");
        group1.addTag("name");
        EventNode e1 = group1.addNode("e1");
        e1.addTag("no2");
        e1.addTag("co2");
        EventNode e2 = group1.addNode("e2");
        e2.addTag("no2");
        e2.addTag("co");
        e2.addTag("co2");

        EventNode group2 = fab1.addNode("group2");
        group2.addTag("name");

        return tree;
    }
}
