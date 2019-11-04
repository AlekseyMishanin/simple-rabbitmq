package ru.mishanin.rabbitmq.produser;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import ru.mishanin.rabbitmq.common.ChannelNameEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class SenderApp {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection = factory.newConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            System.out.println("Selected channel:");
            Stream.of(ChannelNameEnum.values())
                    .map(item->String.format("\n%d\t-\t%s",item.ordinal()+1,item.name()))
                    .forEach(System.out::print);
            System.out.println();
            int channelNumber = getChannelNumber(reader);
            Channel channel = connection.createChannel(channelNumber);
            final String exchange_name = ChannelNameEnum.getNameByOrdinal(channelNumber-1).name();
            channel.exchangeDeclare(exchange_name, "fanout");

            while (true) {
                String message = exchange_name + " " + reader.readLine();
                channel.basicPublish(exchange_name, "", null, message.getBytes("UTF-8"));
                System.out.println("send '" + message + "'");
            }
        }
    }

    private static int getChannelNumber(BufferedReader reader){
        while (true){
            try{
                int channelNumber = Integer.parseInt(reader.readLine());
                return channelNumber;
            }catch (Exception e){
                System.out.println("You enter incorrect data");
            }
        }
    }
}
