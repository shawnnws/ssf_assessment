package vttp2023.batch3.ssf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
 *  This class is to set up the connection to redis database by creating a redis template
 *  This will allow us to establish a connection to redis before we can send our data
 *  Syntax is pretty much the same, just need to configure Host Name, Port Number, User and Password
 *  Different database will have different syntax 
 */


@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost; 

    @Value("${spring.redis.port}")
    private Integer redisPort; 

    @Value("${spring.redis.username}")
       private String redisUser; 

    @Value("${spring.redis.password}")
    private String redisPassword; 

    @Bean
    @Scope("singleton")
    public RedisTemplate<String, Object> getRedisTemplate(){
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);

        if(!redisUser.isEmpty() && !redisPassword.isEmpty()){
            config.setUsername(redisUser);
            config.setPassword(redisPassword);
        }
    config.setDatabase(0);

    final JedisClientConfiguration jdisClient = JedisClientConfiguration.builder().build();

    //setting up connection with redis database
    final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jdisClient);
    //initialize and validate connection factory
    jedisFac.afterPropertiesSet();
    final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

    //associate to redis connection
    redisTemplate.setConnectionFactory(jedisFac);

    /*
     *  Serialization is to change the data to be compatible with redis database before we can send it
     *  Method will change according to datatype
     *  i.e. Set the list key and hash key serialization type to string
     */
    
    //redisTemplate.setKeySerializer(new StringRedisSerializer()); //key for list
    redisTemplate.setHashKeySerializer(new StringRedisSerializer()); //key for hash
            
    //enable redis to store java object on the value column
    //enabling the java object as values in Redis
    RedisSerializer<Object> objSerializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());
    //redisTemplate.setValueSerializer(objSerializer); // value for list
    redisTemplate.setHashValueSerializer(objSerializer); // value for hash

        return redisTemplate;
    }    
 }

