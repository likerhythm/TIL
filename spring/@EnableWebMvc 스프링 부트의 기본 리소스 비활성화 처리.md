`@EnableWebMvc` 어노테이션은 `src/main/resources/static`에 위치한 파일을 자동으로 제공하는 기능을 비활성화 처리한다.
그래서 이 어노테이션을 사용할 경우 해당 파일들을 아래와 같이 수동으로 제공해야 한다.

```java
@Configuration
class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers (ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}
```