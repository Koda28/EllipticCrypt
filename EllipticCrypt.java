package EllipticCrypt;

import java.util.*;

class EllipticCrypt {
    static List<Integer> primeNumbers = new ArrayList<>();

    public static void main(String[] args) {
        int p = 2467;
        primeNumbers = findPrimeNumbers((int) (p * Math.sqrt(p)));
        int a = 313;
        int b = 385;
        TreeMap<Integer, List<Integer>> solution = (solution(a, b, p));
        int n = solution.lastKey();
        int x = solution.get(n).get(0);
        int y = solution.get(n).get(1);
        System.out.println("n -> " + n);
        System.out.println("x -> " +x);
        System.out.println("y -> " +y);
        Random r = new Random();
        int na = (r.nextInt(n - 101) + 100);
        int nb = (r.nextInt(n - 101) + 100);
        System.out.println("NA -> " + na);
        System.out.println("NB -> " + nb);
        var pa = findOpenKey(x, y, na, a, p);
        System.out.println("PA -> " + pa);
        var pb = findOpenKey(x, y, nb, a, p);
        System.out.println("PB -> " + pb);
        var secretKey = findSecretKey(pa.get(0), pa.get(1), nb, a, p);
        if (secretKey.equals(findSecretKey(pb.get(0), pb.get(1), na, a, p))) {
            System.out.println("Секретный ключ -> (" + secretKey.get(0) + ", " + secretKey.get(1) + ")");
            int k = 581;
            System.out.println("Сообщение -> 513, 167");
            var encodeMessage = encodeMessage(x, y, pa, List.of(513, 167), k, a, p);
            System.out.println("Зашифрованное сообщение -> " + encodeMessage);
            System.out.println("Отркытый текст после дешифровки -> " + decodeMessage(encodeMessage, na, a, p));
        } else {
            System.out.println("Секретные ключи разные ");
            System.out.println(secretKey);
            System.out.println(findSecretKey(pb.get(0), pb.get(1), na, a, p));

        }
    }

    public static List<Integer> multipleDotOnNumber(int x, int y, int n, int a, int p) {
        int x1 = x;
        int y1 = y;
        for (int i = 2; i <= n; i++) {
            var res = sumDots(x1, y1, x, y, a, p);
            if (res.get(0) == -1 && res.get(1) == -1) {
                if (i == n) {
                    return Arrays.asList(-1, -1);
                }
                res = multipleDotOnNumber(x, y, n - i, a, p);
                x1 = res.get(0);
                y1 = res.get(1);
                break;
            }
            x1 = res.get(0);
            y1 = res.get(1);
        }
        return Arrays.asList(x1, y1);
    }

    public static List<Integer> multipleDotOnNumber_2(int x, int y, int n, int a, int p) {
        List<Integer> powOf2 = factorizeToPow2(toBin(n));
        var sum = multipleDotOnNumberOfPow2(x, y, powOf2.get(0), a, p);
        for (int i = 1; i < powOf2.size(); i++) {
            var res = multipleDotOnNumberOfPow2(x, y, powOf2.get(i), a, p);
            if (sum.get(0) != -1 && sum.get(1) != -1) {
                if (res.get(0) != -1 && res.get(1) != -1) {
                    sum = sumDots(sum.get(0), sum.get(1), res.get(0), res.get(1), a, p);
                }
            } else if (res.get(0) != -1 && res.get(1) != -1) {
                sum.set(0, res.get(0));
                sum.set(1, res.get(1));
            }
        }
        if (sum.get(0) != -1 && sum.get(1) != -1) {
            return Arrays.asList(sum.get(0), sum.get(1));
        } else return Arrays.asList(-1, -1);
    }

    public static List<Integer> multipleDotOnNumberOfPow2(int x, int y, int n, int a, int p) {
        int x1 = x, y1 = y;
        int k = 1;
        while (k != n) {
            var res = sumDots(x1, y1, x1, y1, a, p);
            if (res.get(0) != -1 && res.get(1) != -1) {
                x1 = res.get(0);
                y1 = res.get(1);
                k *= 2;
            } else {
                return res;
            }
        }
        return Arrays.asList(x1, y1);
    }

    public static List<Integer> sumDots(int x1, int y1, int x2, int y2, int a, int p) {
        int l;
        if (x1 == x2 && ((y1 == -y2) || y1 == -(y2 - p))) {
            return Arrays.asList(-1, -1);
        }
        if (x1 == x2 && y1 == y2) {
            l = divisionMod(3 * x1 * x1 + a, 2 * y1, p);
        } else if (x1 == x2) {
            return Arrays.asList(-1, -1);
        } else {
            l = divisionMod(y2 - y1, x2 - x1, p);
        }
        if(l == -1) {
            return Arrays.asList(-1, -1);
        }

        int x3 = ((l * l - x1 - x2) % p);
        int y3 = ((l * (x1 - x3) - y1) % p);
        if (x3 < 0) {
            x3 += p;
        }
        if (y3 < 0) {
            y3 += p;
        }
        return Arrays.asList(x3, y3);
    }

    public static int divisionMod(int numerator, int denominator, int p) {
        int reverseNumber = reverseNumberByMod(denominator, p);
        if(reverseNumber == 0) {
            return -1;
        }
        int res = (int) (((long) (reverseNumber % p) * numerator) % p);
        return res >= 0 ? res : p + res;
    }

    // b = mod - should be prime
    public static int reverseNumberByMod(int a, int b) {
        a = a % b;
        if(a == 0) {
            return 0;
        }
        if (a < 0) {
            a += b;
        }
        return extendedEuclidAlgorithm(a, b);
    }

    public static int extendedEuclidAlgorithm(int a, int b) {
        int q, t1 = 0, s1, r1, r2, t2, s2 = 0;
        r1 = b;
        r2 = a;
        s1 = 1;
        t2 = 1;
        while (r2 != 0) {
            q = (r1 / r2);
            r1 = r1 - q * r2;
            s1 = s1 - q * s2;
            t1 = t1 - q * t2;

            int c = r1;
            r1 = r2;
            r2 = c;

            c = s1;
            s1 = s2;
            s2 = c;

            c = t1;
            t1 = t2;
            t2 = c;

        }
        return t1;
    }

    public static TreeMap<Integer, List<Integer>> solution(int a, int b, int p) {
        TreeMap<Integer, List<Integer>> map = new TreeMap<>();
        int x;
        int y = 0;
        for (int i = 0; i < p; i++) {
            x = i;
            for (int j = 0; j < p; j++) {
                y = j;
                if ((x * x * x + a * x + b) % p == (y * y) % p) {
                    int n = findMinN(x, y, a, p);
                    if (n != -1) {
                        map.put(n, List.of(x, y));
                    }
                }
            }
            System.out.println("x - " + x + " y -" + y);
        }
        return map;
    }

    private static int findMinN(int x, int y, int a, int p) {
        for (int i = 1; i < p * Math.sqrt(p); i++) {
            var res = multipleDotOnNumber_2(x, y, i, a, p);
            if (res.get(0) == -1 && res.get(1) == -1) {
                if (i > 120 && primeNumbers.contains(i)) {
                    return i;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    /*public static boolean isGenerationalDot(int x, int y, int n, int a, int p) {
        SortedSet<Integer> set = new TreeSet<>();
        for (int i = 1; i < n; i++) {
            var res = multipleDotOnNumber_2(x, y, i, a, p);
            if(res.get(0) != -1)
            set.add(res.get(0));
        }
        return set.size() == p;
    }

     */

    public static List<Integer> findPrimeNumbers(int n) {
        List<Integer> list = new ArrayList<>();
        for (int i = 2; i < n; i++) {
            for (int j = 2; j <= (int) (Math.sqrt(i)) + 1; j++) {
                if ((i % j == 0) && i != j) {
                    break;
                }
                if (j == (int) (Math.sqrt(i)) + 1) {
                    list.add(i);
                }
            }
        }
        return list;
    }

    public static List<Integer> findOpenKey(int xg, int yg, int personalKey, int a, int p) {
        return multipleDotOnNumber(xg, yg, personalKey, a, p);
    }

    public static List<Integer> findSecretKey(int xOpenForeignKey, int yOpenForeignKey, int personalKey, int a, int p) {
        return multipleDotOnNumber(xOpenForeignKey, yOpenForeignKey, personalKey, a, p);
    }

    public static List<Integer> encodeMessage(int x, int y, List<Integer> userAOpenKey, List<Integer> message, int k, int a, int p) {
        var firstDot = multipleDotOnNumber(x, y, k, a, p);
        var kP = multipleDotOnNumber(userAOpenKey.get(0), userAOpenKey.get(1), k, a, p);
        var secondDot = sumDots(message.get(0), message.get(1), kP.get(0), kP.get(1), a, p);
        return List.of(firstDot.get(0), firstDot.get(1), secondDot.get(0), secondDot.get(1));
    }

    public static List<Integer> decodeMessage(List<Integer> encodeMessage, int personalKeyUserA, int a, int p) {
        int x2 = encodeMessage.get(2);
        int y2 = encodeMessage.get(3);
        var n_bkG = multipleDotOnNumber(encodeMessage.get(0), encodeMessage.get(1), personalKeyUserA, a, p);
        var sum = sumDots(x2, y2, n_bkG.get(0), -(n_bkG.get(1) - p), a, p);
        return List.of(sum.get(0), sum.get(1));
    }

    public static String toBin(int n) {
        StringBuilder s = new StringBuilder();
        while (n > 0) {
            s.append(n % 2);
            n /= 2;
        }
        return new StringBuilder(s.toString()).reverse().toString();
    }

    public static List<Integer> factorizeToPow2(String s) {
        var list = new ArrayList<Integer>();
        for (int i = 0; i < s.length(); i++) {
            if (String.valueOf(s.charAt(i)).equals("1")) {
                list.add(pow(2, s.length() - i - 1));
            }
        }
        return list;
    }

    public static int pow(int a, int b) {
        if (b == 0) {
            return 1;
        }
        int res = 1;
        for (int i = 1; i <= b; i++) {
            res *= a;
        }
        return res;
    }

}
  