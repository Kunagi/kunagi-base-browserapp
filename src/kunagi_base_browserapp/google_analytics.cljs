(ns kunagi-base-browserapp.google-analytics)

(defonce !activated (atom false))
(defonce !script-installed (atom false))


(defn- install-script-tag [src text]
  (let [script-tag (.createElement js/document "script")]
    (when src
      (set! (.-src script-tag) src))
    (when text
      (set! (.-text script-tag) text))
    (.appendChild (.-head js/document) script-tag)))


(defn install-script [config]
  (when-not @!script-installed
    (tap> [:dbg ::install-script config])
    (install-script-tag
     (str "https://www.googletagmanager.com/gtag/js?id=" (-> config :tracking-id))
     nil)
    (let [gt-config {"send_page_view" false
                     "anonymize_ip" (get config :anonymize-ip true)
                     "app_name" (get config :app-name "no-app-name")
                     "currency" (get config :currency "EUR")}
          gt-config-s (.stringify js/JSON (clj->js gt-config))]
      (install-script-tag
       nil
       (str "
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());

  gtag('config', '" (-> config :tracking-id) "', " gt-config-s ");
")))
    (reset! !script-installed true)))


(defn activate [config]
  (when-not @!activated
    (tap> [:inf ::activate])
    (install-script config)
    (reset! !activated true)))


(defn deactivate []
  (when @!activated
    (tap> [:inf ::deactivate])
    (reset! !activated false)))
