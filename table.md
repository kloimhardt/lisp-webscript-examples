Within Pehl, using some reader like Spyc, hiccup can be generated out of YAML data:
```
-
  - title: rose
  - price: 1.25
-
  - daisy
  - 0.75
```
[YAMLScript](https://yamlscript.org), which is written in and compiles to Clojure, looks like hiccup without the parentheses (entire code [here](https://github.com/kloimhardt/lisp-webscript-examples/blob/main/table.ys)):
```
defn make-table(rows)::
- <table>
- !
  for [h first(rows)]::
    - <th>
    - !
      key: (first h)
    - </th>
- !
  for [r rows]::
    - <tr>
    - !
      for [v r]::
       - <td>
       - !
         if map?(v):
           val: first(v)
           =>: v
       - </td>
    - </tr>
- </table>
```
