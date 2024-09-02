package dev.ravinda.movie_service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@SpringBootApplication
public class MovieServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieServiceApplication.class, args);
	}

}

@Component
class DataInitializer implements ApplicationListener<ApplicationStartedEvent>{

	private final MovieRepository repository;

	DataInitializer(MovieRepository repository) {
		this.repository = repository;
	}

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		var savedMoviesFlux = Flux.just("Deja Vu", "Seven Pounds", "Titanic", "Up")
				.map(name -> new Movie(null, name))
				.flatMap(repository::save);
		repository
				.deleteAll()
				.thenMany(savedMoviesFlux)
				.thenMany(repository.findAll())
				.subscribe(System.out::println);
	}
}

@Configuration
class MovieRouter {

	private final MovieHandler movieHandler;

	MovieRouter(MovieHandler movieHandler) {
		this.movieHandler = movieHandler;
	}

	@Bean
	public RouterFunction<ServerResponse> route(){
		return RouterFunctions
				.route(GET("/movies").and(accept(MediaType.APPLICATION_JSON)), movieHandler::findAll);
	}

}

@Component
class MovieHandler {
	private final MovieRepository movieRepository;


	MovieHandler(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	public Mono<ServerResponse> findAll(ServerRequest request){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(movieRepository.findAll(), Movie.class);
	}
}

interface MovieRepository extends ReactiveCrudRepository<Movie, Long>{}

@AllArgsConstructor
@Data
@ToString
class Movie {
	@Id
	private Long id;
	private String name;
}