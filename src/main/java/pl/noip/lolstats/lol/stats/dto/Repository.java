package pl.noip.lolstats.lol.stats.dto;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.noip.lolstats.lol.stats.model.Account;

public interface Repository extends MongoRepository<Account, String> {

     Account findByEmail(String email);

}