package io.github.simplydemo;

import org.junit.jupiter.api.Test;

import java.util.Optional;

public class MonadTest {


    @Test
    public void testOptionalMonad() {
        Optional<Integer> maybeNumber = Optional.of(10);

        // flatMap 메서드를 통해 중첩된 컨텍스트를 평탄화할 수 있습니다.
        // flatMap은 첫번째 컨테이너 Optional.of(result) 와 두번째 컨테이너 Optional.empty() 둘 중 하나를 선택하여 반환 할 수 있음.
        Optional<Integer> maybeResult = maybeNumber.flatMap(n -> {
            int result = n * 2;
            return result > 10 ? Optional.of(result) : Optional.empty();
        });
        maybeResult.ifPresent(System.out::println); // 20
    }
}
