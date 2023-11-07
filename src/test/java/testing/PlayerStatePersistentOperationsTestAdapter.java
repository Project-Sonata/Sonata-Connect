package testing;

import com.odeyalo.sonata.connect.entity.PlayerState;
import com.odeyalo.sonata.connect.repository.PlayerStatePersistentOperations;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerStatePersistentOperationsTestAdapter {

    final PlayerStatePersistentOperations testTarget;
    final TestEntityGenerator<? extends PlayerState> entityGenerator;

    protected PlayerState entity;

    public PlayerStatePersistentOperationsTestAdapter(PlayerStatePersistentOperations testTarget,
                                                      TestEntityGenerator<? extends PlayerState> entityGenerator) {
        this.testTarget = testTarget;
        this.entityGenerator = entityGenerator;
    }

    @BeforeAll
    void prepare() {
        PlayerState playerState = entityGenerator.generateValidEntity();
        this.entity = testTarget.save(playerState).block();

    }

    @Test
    public void shouldSave() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result).isNotNull();
    }

    @Test
    void shouldSaveId() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.getId()).isEqualTo(validEntity.getId());
    }

    @Test
    void shouldSavePlayingFlag() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.isPlaying()).isEqualTo(validEntity.isPlaying());
    }

    @Test
    void shouldSaveShuffleState() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.getShuffleState()).isEqualTo(validEntity.getShuffleState());
    }

    @Test
    void shouldSaveRepeatState() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.getRepeatState()).isEqualTo(validEntity.getRepeatState());
    }

    @Test
    void shouldSaveCurrentlyPlayingType() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.getCurrentlyPlayingType()).isEqualTo(validEntity.getCurrentlyPlayingType());
    }

    @Test
    void shouldSaveProgressMs() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.getProgressMs()).isEqualTo(validEntity.getProgressMs());
    }

    @Test
    void shouldSaveUser() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.getUser()).isEqualTo(validEntity.getUser());
    }

    @Test
    void shouldSaveDevices() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.getDevices()).isEqualTo(validEntity.getDevices());
    }

    @Test
    void shouldSavePlayableItem() {
        PlayerState validEntity = entityGenerator.generateValidEntity();

        PlayerState result = testTarget.save(validEntity).block();

        assertThat(result.getCurrentlyPlayingItem()).isNotNull();
    }

    @Test
    public void shouldNotSave() {
        PlayerState entity = entityGenerator.generateInvalidEntity();
        assertThatThrownBy(() -> testTarget.save(entity).block())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldRetrieveEntityById() {
        PlayerState state = testTarget.findById(entity.getId()).block();
        assertThat(state)
                .as("Player state must be found")
                .isNotNull()
                .isEqualTo(entity);
    }

    @Test
    public void shouldNullForNotExistingId() {
        PlayerState state = testTarget.findById(-1L).block();
        assertThat(state)
                .as("Player state must be null if ID does not exist")
                .isNull();
    }

    @Test
    public void shouldDeleteById() {
        testTarget.deleteById(entity.getId()).block();

        PlayerState afterDeletion = testTarget.findById(entity.getId()).block();

        assertThat(afterDeletion)
                .as("After deletion player should be deleted")
                .isNull();
    }

    @Test
    public void shouldNotDeleteByNotExistingId() {
        Long beforeDeletion = testTarget.count().block();
        testTarget.deleteById(-1L).block();
        Long afterDeletion = testTarget.count().block();

        assertThat(afterDeletion)
                .as("The size should not be changed, if ID does not exist")
                .isEqualTo(beforeDeletion);
    }

    @Test
    public void shouldFindByUserId() {
        PlayerState result = this.testTarget.findByUserId(entity.getUser().getId()).block();

        assertThat(result)
                .as("Entity must be found by user id!")
                .isNotNull()
                .isEqualTo(entity);
    }

    @Test
    void shouldClear() {
        testTarget.clear().block();

        Long afterClear = testTarget.count().block();

        assertThat(afterClear).isEqualTo(0);
    }
}