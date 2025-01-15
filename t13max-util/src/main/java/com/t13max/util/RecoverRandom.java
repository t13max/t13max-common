package com.t13max.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author t13max
 * @since 14:20 2025/1/15
 */
public class RecoverRandom {

    private final Random random;

    private final long seed;

    private final AtomicInteger count = new AtomicInteger();

    public RecoverRandom(long seed) {
        this.random = new Random(seed);
        this.seed = seed;
    }

    public RecoverRandom(long seed, int count) {
        this(seed);
        for (int i = 0; i < count; i++) {
            this.random.nextInt();
            this.count.incrementAndGet();
        }
    }

    public int getCount() {
        return count.get();
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean gaiLvTo(double num) {
        if (num <= 0.0) {
            return false;
        } else if (num >= 100.0) {
            return true;
        } else {
            return getRanNumByIntervalDouble(0.0, 100.0) < num;
        }
    }

    public int nextInt(int min, int max) {
        if (min == max) {
            return min;
        } else {
            if (max < min) {
                int oldMax = max;
                max = min;
                min = oldMax;
            }
            this.count.incrementAndGet();
            return (int) (random.nextDouble() * (double) (max - min + 1)) + min;
        }
    }

    public int nextInt(int max) {
        return nextInt(0, max);
    }

    public long nextInt(long min, long max) {
        if (min == max) {
            return min;
        } else {
            if (max < min) {
                long oldMax = max;
                max = min;
                min = oldMax;
            }

            return (long) (random.nextDouble() * (double) (max - min + 1L)) + min;
        }
    }

    public double getRanNumByIntervalDouble(double min, double max) {
        if (min == max) {
            return min;
        } else {
            if (max < min) {
                double oldMax = max;
                max = min;
                min = oldMax;
            }
            this.count.incrementAndGet();
            return random.nextDouble() * (max - min) + min;
        }
    }

    public Set<Integer> getRanListByInterval(int min, int max, int n) {
        Set<Integer> list = new HashSet<>();
        n = Math.min(n, max - min + 1);

        for (int i = 0; i < n; ++i) {
            if (list.size() == n) {
                return list;
            }
            this.count.incrementAndGet();
            int num = (int) (random.nextDouble() * (double) (max - min + 1)) + min;
            if (list.contains(num)) {
                --i;
            } else {
                list.add(num);
            }
        }

        return list;
    }

    public int randomIndexByWeight(int[] weight) {
        if (Objects.isNull(weight)) {
            throw new IllegalArgumentException("weightArray is null");
        } else if (weight.length == 0) {
            throw new IllegalArgumentException("weightArray.length==0");
        } else {
            int total = 0;
            int index = weight.length;

            int k;
            for (k = 0; k < index; ++k) {
                int num = weight[k];
                if (num > 0) {
                    total += num;
                }
            }

            int randNum = nextInt(1, total);
            index = 0;
            total = 0;

            for (k = 0; k < weight.length; ++k) {
                total += weight[k];
                if (randNum <= total) {
                    index = k;
                    break;
                }
            }

            return index;
        }
    }

    public <T> T random(List<T> list, Function<T, Integer> function) {
        if (list != null && !list.isEmpty()) {
            int[] weights = new int[list.size()];

            int index;
            for (index = 0; index < list.size(); ++index) {
                weights[index] = function.apply(list.get(index));
            }

            index = randomIndexByWeight(weights);
            return list.get(index);
        } else {
            return null;
        }
    }

    public <T> T random(T[] array, Function<T, Integer> function) {
        if (array != null && array.length != 0) {
            int[] weights = new int[array.length];

            int index;
            for (index = 0; index < array.length; ++index) {
                weights[index] = (Integer) function.apply(array[index]);
            }

            index = randomIndexByWeight(weights);
            return array[index];
        } else {
            return null;
        }
    }

    public <T> List<T> randoms(List<T> list, int resultNum, Function<T, Integer> function) {
        if (list != null && !list.isEmpty()) {
            int[] weights = new int[list.size()];

            for (int i = 0; i < list.size(); ++i) {
                weights[i] = function.apply(list.get(i));
            }

            List<T> listResult = new LinkedList<>();

            for (int i = 0; i < resultNum; ++i) {
                int index = randomIndexByWeight(weights);
                listResult.add(list.get(index));
            }

            return listResult;
        } else {
            return null;
        }
    }

    public <T> T random(List<T> list) {
        if (list != null && !list.isEmpty()) {
            int index = nextInt(list.size() - 1);
            return list.get(index);
        } else {
            return null;
        }
    }

    public <T> T random(T[] array) {
        if (array != null && array.length != 0) {
            int index = nextInt(array.length - 1);
            return array[index];
        } else {
            return null;
        }
    }

    public <T> List<T> random(T[] array, int num) {
        Set<T> result = new HashSet<>();

        while (result.size() != num) {
            int index = nextInt(array.length - 1);
            result.add(array[index]);
        }

        return new ArrayList<>(result);
    }

    public <T> List<T> random(List<T> list, int num) {
        if (list != null && !list.isEmpty() && list.size() >= num) {
            Set<T> result = new HashSet<>();

            while (result.size() < num) {
                int index = nextInt(list.size() - 1);
                result.add(list.get(index));
            }

            return new LinkedList<>(result);
        } else {
            return null;
        }
    }

    public int getRandomByWeight(Map<Integer, Integer> map) {
        int max = 0;
        List<Integer> idlist = new ArrayList<>();
        List<Integer> probabilitylist = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            idlist.add(entry.getKey());
            max += entry.getValue();
            probabilitylist.add(max);
        }

        int ranNumByInterval = nextInt(1, max);

        for (int x = 0; x < probabilitylist.size(); ++x) {
            if (probabilitylist.get(x) >= ranNumByInterval) {
                return idlist.get(x);
            }
        }

        return -1;
    }

    public int getRandomByWeight(String randomString) {
        int max = 0;
        List<Integer> idlist = new ArrayList<>();
        List<Integer> probabilitylist = new ArrayList<>();
        String[] split = randomString.split(";");
        int x = split.length;

        for (int var7 = 0; var7 < x; ++var7) {
            String s = split[var7];
            String[] split1 = s.split(",");
            idlist.add(Integer.parseInt(split1[0]));
            max += Integer.parseInt(split1[1]);
            probabilitylist.add(max);
        }

        int ranNumByInterval = nextInt(1, max);

        for (x = 0; x < probabilitylist.size(); ++x) {
            if (probabilitylist.get(x) >= ranNumByInterval) {
                return idlist.get(x);
            }
        }

        return -1;
    }

    public Set<Integer> getRandomManyByWeight(String randomString, int num) {
        int numInit = num;
        Set<Integer> randomIds = new HashSet<>();

        for (int i = 0; i < num && num - numInit < 10; ++i) {
            int attributeId = getRandomByWeight(randomString);
            if (randomIds.contains(attributeId)) {
                ++num;
            } else {
                randomIds.add(attributeId);
            }
        }

        return randomIds;
    }

    public Set<Integer> random(String str, int resultNum) {
        return random(str, resultNum, ",");
    }

    public Set<Integer> random(String str, int resultNum, String splitStr) {
        Set<Integer> result = new HashSet<>();
        if (resultNum > 0) {
            List<Integer> list = new LinkedList<>();
            if (splitStr.equals(",")) {
                list = StringUtil.getIntList(str);
            }

            if (resultNum >= (list).size()) {
                result.addAll(list);
            } else {
                while (!list.isEmpty() && result.size() < resultNum) {
                    Integer random = random(list);
                    result.add(random);
                    list.remove(random);
                }
            }

        }
        return result;
    }
}
