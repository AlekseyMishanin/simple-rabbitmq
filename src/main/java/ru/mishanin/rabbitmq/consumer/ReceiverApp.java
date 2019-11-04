package ru.mishanin.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import ru.mishanin.rabbitmq.common.ChannelNameEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class ReceiverApp {

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection = factory.newConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
        System.out.println("Selected channel (/chch 'numberChannel'):");
        Stream.of(ChannelNameEnum.values())
                .map(item->String.format("\n%d\t-\t%s",item.ordinal()+1,item.name()))
                .forEach(System.out::print);
        Channel channel = null;
        while (true) {
            int channelNumber = getChannelNumber(reader);
            if(channel!=null) channel.close();
            channel = connection.createChannel(channelNumber);
            final String exchange_name = ChannelNameEnum.getNameByOrdinal(channelNumber-1).name();
            channel.exchangeDeclare(exchange_name, "fanout");
            String qName = channel.queueDeclare().getQueue();
            channel.queueBind(qName, exchange_name, "");
            System.out.println("Waiting for message from channel " + exchange_name);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("Received '" + message + "'");
            };

            channel.basicConsume(qName, true, deliverCallback, consumerTag -> {
            });
        }
    }}

    private static int getChannelNumber(BufferedReader reader) throws IOException {

        String[] strArr;
        while (true){
            System.out.println();
            String strLine = reader.readLine();
            strArr = strLine.split(" ", 2);
            if(!strLine.startsWith("/chch ")) {
                System.out.println("You enter incorrect data");
            } else {
                break;
            }
        }
        int channelNumber = Integer.parseInt(strArr[1]);
        return channelNumber;
    }
}
