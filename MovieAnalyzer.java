import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {

    String data_path;

    public static List<String[]> s_readfile(String dataset_path) throws IOException {

        List<String[]> movielist = new ArrayList<>();
        FileReader f = null;
        BufferedReader bf = null;
        try {
            f = new FileReader(dataset_path);
            bf = new BufferedReader(f);
            String str;
            str = bf.readLine();
            while ((str = bf.readLine()) != null) {
                //System.out.println(str);
                String[] s = new String[16];
                char[] characters = str.toCharArray();
                int jk = -1;
                int index = 0;
                boolean is = true;
                int num = 0;
                for (int i = 0; i < characters.length; i++) {
                    if (characters[i] == '\"') {
                        num++;
                        is = true;
                        continue;
                    }
                    if (characters[i] == ',') {
                        if (num % 2 == 0 || !is) {
                            s[index] = str.substring(jk + 1, i);
                            index++;
                            is = false;
                            jk = i;
                            num = 0;
                        }

                    }
                }
                s[15] = str.substring(jk + 1);
                movielist.add(s);
            }
            bf.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bf) {
                    bf.close();
                }
                if (null != f) {
                    f.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return movielist;
    }


    public static class Movie {


        public String Poster_Link;
        public String Series_Title;

        public int getReleased_Year() {
            return Released_Year;
        }

        public int Released_Year;
        public String Certificate;
        public String Runtime;

        public String[] getGenre() {
            return Genre;
        }

        public String[] Genre;
        public String allGenre;
        public String IMDB_Rating;

        public String getOverview() {
            return Overview;
        }

        public String Overview;
        public String Meta_score;
        public String Director;
        public String Star1;
        public String Star2;
        public String Star3;
        public String Star4;
        public String No_of_Votes;
        public String Gross;

        public String getStar1() {
            return Star1;
        }

        public String getStar2() {
            return Star2;
        }

        public String getStar3() {
            return Star3;
        }

        public String getStar4() {
            return Star4;
        }

        public String getRuntime() {
            return Runtime;
        }

        public String getSeries_Title() {
            return Series_Title;
        }

        public String getIMDB_Rating() {
            return IMDB_Rating;
        }

        public String getGross() {
            return Gross;
        }

        public Movie(String[] a
        ) {
            this.Poster_Link = a[0];
            this.Series_Title = a[1].replaceAll("\"", "");
            this.Released_Year = Integer.parseInt(a[2]);
            this.Certificate = a[3];
            this.Runtime = a[4];
            this.Genre = a[5].replaceAll("\"", "").replaceAll(" ", "").split(",");
            this.allGenre = a[5];
            this.IMDB_Rating = a[6];
            this.Overview = a[7];
            this.Meta_score = a[8];
            this.Director = a[9];
            this.Star1 = a[10];
            this.Star2 = a[11];
            this.Star3 = a[12];
            this.Star4 = a[13];
            this.No_of_Votes = a[14];
            this.Gross = a[15];
        }

    }

    public MovieAnalyzer(String dataset_path) throws IOException {

        data_path = dataset_path;
        movie = new ArrayList<>();
        Files.lines(Paths.get(dataset_path)).skip(1).map(l -> getline(l)).forEach(l -> movie.add(l));
    }

    public static String[] getline(String line) {
        String[] s = new String[16];
        char[] characters = line.toCharArray();
        int j = -1;
        int index = 0;
        boolean is = true;
        int num = 0;
        for (int i = 0; i < characters.length; i++) {
            if (characters[i] == '\"') {
                num++;
                is = true;
                continue;
            }
            if (characters[i] == ',') {
                if (num % 2 == 0 || !is) {
                    s[index] = line.substring(j + 1, i);
                    index++;
                    is = false;
                    j = i;
                    num = 0;
                }

            }
        }
        s[15] = line.substring(j + 1);
        return s;
    }

    public List<String[]> movie;

    public void setMovie() throws IOException {
        movie = s_readfile(data_path);
    }

    public List<String[]> getMovie() throws IOException {
        return movie;
    }


    public Map<Integer, Integer> getMovieCountByYear() throws IOException {
        Stream<Movie> movielist = movie.stream().map(Movie::new);
        Map<Integer, Long> map1 = movielist.collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.counting()));
        Set<Integer> mapkey = map1.keySet();
        Map<Integer, Integer> MovieCountByYear = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        mapkey.forEach(a -> {
            Long A1 = map1.get(a);
            Integer A2 = A1.intValue();
            MovieCountByYear.put(a, A2);
        });
        return MovieCountByYear;

    }

    public Map<String, Integer> getMovieCountByGenre() throws IOException {
        Stream<Movie> movielist = movie.stream().map(Movie::new);
        Map<String, Integer> MovieCountByGenre = new LinkedHashMap<>();

        movielist.forEach(s -> {
            String[] m = s.getGenre();
            for (int i = 0; i < m.length; i++) {
                if (MovieCountByGenre.containsKey(m[i])) {
                    MovieCountByGenre.replace(m[i], MovieCountByGenre.get(m[i]), MovieCountByGenre.get(m[i]) + 1);
                } else MovieCountByGenre.put(m[i], 1);
            }
        });

        ArrayList<String> jk = new ArrayList<>();
        MovieCountByGenre.forEach((m, n) -> {
            jk.add(m + ";" + n);
        });
        Map<String, Integer> answer = new LinkedHashMap<>();
        jk.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (Integer.parseInt(o1.split(";")[1]) == Integer.parseInt(o2.split(";")[1])) {
                    return o1.compareTo(o2);
                }
                return Integer.parseInt(o2.split(";")[1]) - Integer.parseInt(o1.split(";")[1]);
            }
        });

        for (String s : jk) {
            String[] dk = s.split(";");
            answer.put(dk[0], Integer.parseInt(dk[1]));
        }
        return answer;

    }

    public Map<List<String>, Integer> getCoStarCount() throws IOException {
        Stream<Movie> movielist = movie.stream().map(Movie::new);
        Map<String, Integer> map = new HashMap<String, Integer>();
        movielist.forEach(a -> {
            add(map, a.Star1, a.Star2);
            add(map, a.Star1, a.Star3);
            add(map, a.Star1, a.Star4);
            add(map, a.Star2, a.Star3);
            add(map, a.Star2, a.Star4);
            add(map, a.Star3, a.Star4);
        });
        ArrayList<String> strings = new ArrayList<String>();
        map.forEach((k, v) -> {
            strings.add(k + ";" + v);
        });
        strings.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i1 = Integer.parseInt(o1.split(";")[1]);
                int i2 = Integer.parseInt(o2.split(";")[1]);
                if (i1 == i2) {
                    String[] s1 = o1.split(",");
                    String[] s2 = o2.split(",");
                    if (s1[0].compareTo(s2[0]) == 0) {
                        return s1[1].compareTo(s2[1]);
                    }
                    return 0;
                } else {
                    return i2 - i1;
                }
            }
        });
        Map<List<String>, Integer> ans = new LinkedHashMap<>();
        strings.forEach(a -> {
            List<String> list = new ArrayList<>();
            String[] ss = a.split(";");
            list.add(ss[0].split(",")[0]);
            list.add(ss[0].split(",")[1]);
            ans.put(list, Integer.parseInt(ss[1]));
        });
        return ans;
    }

    public static void add(Map<String, Integer> map, String s1, String s2) {
        if (s1.length() == 0 || s2.length() == 0) {
            return;
        }
        String s;
        if (s2.compareTo(s1) >= 0) {
            s = s1 + "," + s2;
        } else {
            s = s2 + "," + s1;
        }
        if (map.containsKey(s)) {
            int data = map.get(s);
            map.replace(s, data, data + 1);
        } else {

            map.put(s, 1);
        }

    }

    public List<String> getTopMovies(int top_k, String by) throws IOException {
        Stream<Movie> movielist = movie.stream().map(Movie::new);
        Map<String, Integer> T = new LinkedHashMap<>();
        if (by.equals("runtime")) {
            ArrayList<String> n1 = new ArrayList<>();
            movielist.forEach(s -> {
                n1.add(s.getSeries_Title() + ";" + s.getRuntime().split(" ")[0]);
            });
            n1.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (Integer.parseInt(o1.split(";")[1]) == Integer.parseInt(o2.split(";")[1])) {
                        return o1.split(";")[0].compareTo(o2.split(";")[0]);
                    }
                    return Integer.parseInt(o2.split(";")[1]) - Integer.parseInt(o1.split(";")[1]);
                }
            });
            List<String> runtime = new ArrayList<>();
            for (int i = 0; i < top_k; i++) {
                runtime.add(n1.get(i).split(";")[0]);
            }
            return runtime;
        }
        if (by.equals("overview")) {
            ArrayList<String> n1 = new ArrayList<>();
            movielist.forEach(s -> {
                n1.add(s.getSeries_Title() + ";" + s.getOverview());

            });
            n1.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int o1l = o1.split(";")[1].length();
                    int o2l = o2.split(";")[1].length();
                    if (o1.split(";")[1].substring(o1.split(";")[1].length() - 1).equals("\"")) {
                        o1l -= 2;
                    }
                    if (o2.split(";")[1].substring(o2.split(";")[1].length() - 1).equals("\"")) {
                        o2l -= 2;
                    }
                    if (o1l == o2l) {
                        return o1.split(";")[0].compareTo(o2.split(";")[0]);
                    } else return o2l - o1l;
                }
            });
            List<String> overview = new ArrayList<>();
            for (int i = 0; i < top_k; i++) {
                overview.add(n1.get(i).split(";")[0]);
            }
            return overview;

        } else {return null;}
    }

    public List<String> getTopStars(int top_k, String by) throws IOException {
        Stream<Movie> movielist = movie.stream().map(Movie::new);
        Map<String, Float[]> T = new LinkedHashMap<>();
        List<String> getTopStars = new ArrayList<>();
        if (by == "rating") {
            movielist.forEach(s -> {
                        String m1 = s.getStar1();
                        String m2 = s.getStar2();
                        String m3 = s.getStar3();
                        String m4 = s.getStar4();
                        String[] m = new String[4];
                        m[0] = m1;
                        m[1] = m2;
                        m[2] = m3;
                        m[3] = m4;
                        for (int i = 0; i < 4; i++) {
                            Float[] M = new Float[1];
                            M[0] = Float.parseFloat(s.getIMDB_Rating());
                            if (T.containsKey(m[i])) {
                                Float[] g = new Float[T.get(m[i]).length + 1];
                                for (int j = 0; j < T.get(m[i]).length; j++) {
                                    g[j] = T.get(m[i])[j];
                                }
                                g[g.length - 1] = M[0];
                                T.replace(m[i], T.get(m[i]), g);
                            } else T.put(m[i], M);
                        }
                    }
            );
            Map<String, Double> t = new LinkedHashMap<>();
            for (String key : T.keySet()) {
                Double o = 0.0;
                for (int i = 0; i < T.get(key).length; i++) {
                    o += (double) T.get(key)[i];
                }
                Double z = (double) (o / T.get(key).length);

                t.put(key, z);
            }
            ArrayList<String> tmp = new ArrayList<>();
            t.forEach((m, n) -> tmp.add(m + ";" + n));
            tmp.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    Double o1n = Double.parseDouble(o1.split(";")[1]);
                    Double o2n = Double.parseDouble(o2.split(";")[1]);
                    if (o1n.equals(o2n)) {
                        return o1.compareTo(o2);
                    }
                    return o2n.compareTo(o1n);
                }
            });

            for (int i = 0; i < top_k; i++) {
                getTopStars.add(tmp.get(i).split(";")[0]);
            }
            return getTopStars;
        }
        if (by.equals("gross")) {
            Map<String, String[]> listMap2 = new LinkedHashMap<>();
            movielist.forEach(s -> {
                        String m1 = s.getStar1();
                        String m2 = s.getStar2();
                        String m3 = s.getStar3();
                        String m4 = s.getStar4();
                        String m[] = new String[4];
                        m[0] = m1;
                        m[1] = m2;
                        m[2] = m3;
                        m[3] = m4;
                        for (int i = 0; i < 4; i++) {
                            String[] M = new String[1];
                            String P = s.getGross();
                            if (s.getGross().equals("")) {
                                continue;
                            }

                            M[0] = P;
                            if (listMap2.containsKey(m[i])) {
                                String[] g = new String[listMap2.get(m[i]).length + 1];
                                for (int j = 0; j < listMap2.get(m[i]).length; j++) {
                                    g[j] = listMap2.get(m[i])[j];
                                }
                                g[g.length - 1] = M[0];
                                listMap2.replace(m[i], listMap2.get(m[i]), g);
                            } else listMap2.put(m[i], M);
                        }
                    }
            );

            ArrayList<String> arrayList = new ArrayList<>();
            listMap2.forEach((k, v) -> {
                Long al = 0L;
                for (String value : v) {
                    al += Long.parseLong(value.replaceAll(",", "").replaceAll("\"", ""));
                }
                if (al != 0) {
                    al /= v.length;
                }

                String s = k + ";" + al;
                arrayList.add(s);
            });
            arrayList.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int o1n = Integer.parseInt(o1.split(";")[1]);
                    int o2n = Integer.parseInt(o2.split(";")[1]);
                    if (o1n == o2n) {
                        return o1.compareTo(o2);
                    }
                    return o2n - o1n;
                }
            });
            for (int i = 0; i < top_k; i++) {
                getTopStars.add(arrayList.get(i).split(";")[0]);
            }

        }
        return getTopStars;


    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) throws IOException {
        Stream<Movie> movielist = movie.stream().map(Movie::new);
        List<String> searchMovies = new ArrayList<>();
        movielist.filter(s -> (Integer.parseInt(s.getRuntime().split(" ")[0]) <= max_runtime) && (Float.parseFloat(s.getIMDB_Rating()) >= min_rating)).forEach(s -> {
            for (int i = 0; i < s.getGenre().length; i++) {
                if (s.getGenre()[i].equals(genre)) {
                    searchMovies.add(s.getSeries_Title());
                }
            }
        });
        searchMovies.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return searchMovies;

    }

    public static void main(String args[]) throws IOException {
        MovieAnalyzer movieAnalyzer = new MovieAnalyzer("D:\\A1-sample\\A1_Sample\\resources\\imdb_top_500.csv");
        System.out.println(movieAnalyzer.getTopStars(20, "gross"));
    }


}
