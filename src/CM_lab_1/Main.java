package CM_lab_1;

import UTFE.TableOutput.*;

import java.util.ArrayList;

class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}

public class Main {
    interface Function {
        double calc(double x);
    }

    static void Tabulating(double a, double b, double step, Function f) {
        ArrayList<Object[]> listTable = new ArrayList<>();
        listTable.add(new Object[]{"x", "y=f(x)", "sign"});
        double EPS = Math.pow(10, -6);
        for (double x = a; x - EPS <= b; x += step) {
            double y = f.calc(x);
            listTable.add(new Object[]{x, y, y >= 0 ? "+" : "-"});
        }

        Object[][] outputTable = new Object[listTable.size()][];
        for (int i = 0; i < listTable.size(); ++i) {
            outputTable[i] = listTable.get(i).clone();
            if (i > 0) outputTable[i][2] = " ";
        }

        for (int i = 1; i < listTable.size() - 1; ++i) {
            if (!listTable.get(i)[2].equals(listTable.get(i + 1)[2])) {
                outputTable[i][2] = listTable.get(i)[2];
                outputTable[i + 1][2] = listTable.get(i + 1)[2];
            }
        }
        System.out.println(Table.TableToString(outputTable));
    }


    public static Tuple<Double, Integer> BisectionMethod(double a, double b, Function f, int eps, boolean printTable) {
        if (f.calc(a) * f.calc(b) >= 0) {
            System.out.println("Функция не имеет корней");
            return new Tuple<>(0.0, 0);
        }
        ArrayList<Object[]> table = new ArrayList<>();
        table.add(new Object[]{"n", "An", "Cn", "Bn", "f(An)", "f(Cn)", "f(Bn)", "|An-Bn|"});
        int n = 0;
        double c = (a + b) / 2;
        double fc = f.calc(c);
        boolean anotherOne = false;
        while (b - a >= Math.pow(10, eps) || anotherOne) {
            c = (a + b) / 2;
            double fa = f.calc(a);
            double fb = f.calc(b);
            fc = f.calc(c);
            table.add(new Object[]{n, a, c, b, fa, fc, fb, b - a});
            if (fa * fc >= 0) a = c;
            else b = c;
            anotherOne = b - a < Math.pow(10, eps) && !anotherOne;
            n++;
        }
        if (printTable) {
            System.out.println("\tМетод бисекции");
            System.out.println(Table.TableToString(table.toArray(Object[][]::new)));
        }
        return new Tuple<>(c, n - 1);
    }

    public static Tuple<Double, Integer> NewtonMethod(Function func, Function dfunc, double x0, int eps, boolean printTable) {
        ArrayList<Object[]> table = new ArrayList<>();
        table.add(new Object[]{"n", "x", "f(x)", "f'(x)", "|Xn-X(n-1)|"});
        int n = 0;
        double x1 = x0 - func.calc(x0) / dfunc.calc(x0);
        boolean anotherOne = false;
        while (Math.abs(x1 - x0) >= Math.pow(10, eps) || anotherOne) {
            table.add(new Object[]{n, x0, func.calc(x0), dfunc.calc(x0), Math.abs(x1 - x0)});
            x0 = x1;
            x1 = x0 - func.calc(x0) / dfunc.calc(x0);
            n++;
            //anotherOne = Math.abs(x1 - x0) < Math.pow(10, eps) && !anotherOne;
        }
        table.add(new Object[]{n, x0, func.calc(x0), dfunc.calc(x0), Math.abs(x1 - x0)});

        if (printTable) {
            System.out.println("\tМетод Ньютона");
            System.out.println(Table.TableToString(table.toArray(Object[][]::new)));
        }
        return new Tuple<>(x0, n);
    }

    public static Tuple<Double, Integer> ChordMethod(Function func, double x0, double x1, int eps, boolean printTable) {
        ArrayList<Object[]> table = new ArrayList<>();
        table.add(new Object[]{"n", "X(n+1)", "X(n-1)", "X(n)", "f(X(n-1))", "f(Xn)", "|Xn-X(n-1)|"});
        int n = 0;
        ArrayList<Double> x = new ArrayList<>();
        x.add(x0);
        x.add(x1);
        boolean anotherOne = false;
        x.add(x.get(n) - func.calc(x.get(n)) * (x.get(n + 1) - x.get(n)) / (func.calc(x.get(n + 1)) - func.calc(x.get(n))));
        while (Math.abs(x.get(n + 2) - x.get(n + 1)) >= Math.pow(10, eps) || anotherOne) {
            table.add(new Object[]{n, x.get(n + 2), x.get(n), x.get(n + 1), func.calc(x.get(n)), func.calc(x.get(n + 1)), Math.abs(x.get(n + 1) - x.get(n))});
            n++;
            x.add(x.get(n) - func.calc(x.get(n)) * (x.get(n + 1) - x.get(n)) / (func.calc(x.get(n + 1)) - func.calc(x.get(n))));
            anotherOne = Math.abs(x.get(n + 2) - x.get(n + 1)) < Math.pow(10, eps) && !anotherOne;
        }
        table.add(new Object[]{n, x.get(n + 2), x.get(n), x.get(n + 1), func.calc(x.get(n)), func.calc(x.get(n + 1)), Math.abs(x.get(n + 1) - x.get(n))});
        if (printTable) {
            System.out.println("\tМетод Хорд");
            System.out.println(Table.TableToString(table.toArray(Object[][]::new)));
        }
        return new Tuple<>(x.get(x.size() - 2), n);
    }


    public static Tuple<Double, Integer> SimpleIterationMethod(Function func, double x0, double eps, boolean printTable) {
        ArrayList<Object[]> table = new ArrayList<>();
        table.add(new Object[]{"n", "X(n-1)", "X(n)", "|Xn-X(n-1)|"});
        int n = 0;
        double x1 = func.calc(x0);
        boolean anotherOne = false;
        while (Math.abs(x1 - x0) >= Math.pow(10, eps) || anotherOne) {
            table.add(new Object[]{n, x0, x1, Math.abs(x1 - x0)});
            x0 = x1;
            x1 = func.calc(x0);
            n++;
            anotherOne = Math.abs(x1 - x0) < Math.pow(10, eps) && !anotherOne;
        }
        //table.add(new Object[]{n, x0, x1, Math.abs(x1 - x0)});
        if (printTable) {
            System.out.println("\tМетод простых итераций");
            System.out.println(Table.TableToString(table.toArray(Object[][]::new)));
        }
        return new Tuple<>(x0, n - 1);
    }

    public static void main(String[] args) {
        Table.SetDecimalPlaces(15);

        Function f = x -> 2 * Math.atan(x) - 3 * x + 2;
        Function phi = x -> 2.0 / 3.0 * (Math.atan(x) + 1);
        Function df = x -> 2 / (x * x + 1) - 3;

        int leftEps = 2;
        int rightEps = 6;

        int tableCount = 4;
        ArrayList<Object[]>[] epsTables = new ArrayList[tableCount];
        for (int i = 0; i < tableCount; ++i){
            epsTables[i] = new ArrayList<>();
            epsTables[i].add(new Object[]{"Точность E", "Корень", "n"});
        }

        for (int eps = leftEps; eps <= rightEps; ++eps) {
            Tuple<Double, Integer>[] tuples = new Tuple[tableCount];
            tuples[0] = BisectionMethod(0, 3, f, -eps, eps == rightEps);
            tuples[1] = NewtonMethod(f, df, 0, -eps, eps == rightEps);
            tuples[2] = ChordMethod(f, 0, 3, -eps, eps == rightEps);
            tuples[3] = SimpleIterationMethod(phi, 3, -eps, eps == rightEps);
            for(int i = 0; i < tableCount; ++i) epsTables[i].add(new Object[]{String.format("10^(-%d)", eps), tuples[i].x, tuples[i].y});
        }
        for(int i = 0; i < tableCount; ++i) {
            String name = switch (i) {
                case 0 -> "Метод бисекции";
                case 1 -> "Метод Ньютона";
                case 2 -> "Метод хорд";
                case 3 -> "Метод простых итераций";
                default -> "";
            };
            System.out.println("\t" + name);
            System.out.println(Table.TableToString(epsTables[i].toArray(Object[][]::new)));
        }

    }
}
