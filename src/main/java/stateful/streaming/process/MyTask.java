package stateful.streaming.process;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;

/**
 * 
 * @author yingkaihu
 * 
 */
public class MyTask implements StreamTask, InitableTask, WindowableTask {

	private static final SystemStream OUTPUT_STREAM = new SystemStream("kafka",
			"testWords");

	private KeyValueStore<String, String> meta;
	private int eventsSeen = 0;
			
	public MyTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(IncomingMessageEnvelope envelope,
			MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		String message = (String) envelope.getMessage();
		for (String word : message.split(" ")) {
			String count = meta.get(word);
			count = count == null ? String.valueOf(1) : String.valueOf(Integer
					.parseInt(count) + 1);
			meta.put(word, count);
			collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM, word+"-"+count));
			eventsSeen++;
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(Config conf, TaskContext context) throws Exception {
		meta = (KeyValueStore<String, String>) context.getStore("meta-counter");
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator)
			throws Exception {
		collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM, Integer.toString(eventsSeen)));
	}

}
