(ns redakti.buffer)


(defn path-without-last
  "Remove the last element of a `path`.
  `(path-without-last [1 0 5]) -> [1 0]`"
  [path]
  (when path
    (->> path reverse rest reverse (into []))))


(defn node-by-path
  "Returns the child node of `node` identified by `path`"
  [node path]
  (when node
    (if (empty? path)
      node
      (let [idx (first path)
            child (-> node :navigator.node/nodes (nth idx))]
        (when child
          (node-by-path child (rest path)))))))


(defn path-parent [path tree]
  (if (empty? path)
    nil
    (path-without-last path)))


(defn path-first-child [path tree]
  (if (empty? (-> (node-by-path tree path) :navigator.node/nodes))
    nil
    (conj path 0)))


(defn path-prev [path tree]
  (if (empty? path)
    nil
    (let [idx (last path)]
      (if (= 0 idx)
        (path-parent path tree)
        (assoc path (-> path count dec) (dec idx))))))


(defn path-next [path tree]
  (if (empty? path)
    nil
    (let [idx (last path)
          parent-path (path-without-last path)
          limit (-> (node-by-path tree parent-path) :navigator.node/nodes count dec)]
      (if (>= idx limit)
        (path-parent path tree)
        (assoc path (-> path count dec) (inc idx))))))


(defn path-down [path tree]
  (path-next path tree))


(defn path-up [path tree]
  (path-prev path tree))


(defn path-left [path tree]
  (path-parent path tree))


(defn path-right [path tree]
  (path-first-child path tree))



;;; creating / updating

(defn leaf [& component]
  {:navigator.node/type :leaf
   :navigator.node/component component})


(defn column [& nodes]
  {:navigator.node/type :column
   :navigator.node/nodes nodes})


(defn dummy-tree []
  (column
   (leaf)
   (column
    (leaf)
    (leaf)
    (leaf))
   (leaf)
   (leaf)))


(defn new-buffer []
  {:cursor []
   :tree (dummy-tree)})


;;; keys


(defn cursor-after-key [{:keys [tree cursor] :as buffer} key]
  (js/console.log key buffer)
  (let [r
          (or (case key
                "j" (path-down cursor tree)
                "k" (path-up cursor tree)
                "h" (path-left cursor tree)
                "l" (path-right cursor tree)
                nil)
              cursor)]
    (js/console.log "->" r)
    r))


(defn process-key [buffer key]
  (assoc buffer :cursor (cursor-after-key buffer key)))
