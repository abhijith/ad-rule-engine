#### Request-Response

* extract channel from request
  - validate country and channel

	=> invalid -> return 404

	=> valid
      - extract categories and associate to request

      - for each available-ads (live and limits not exhausted)

        -> evaluate constraints with categories, country, language filled in:
           => select the first ad found / based on ranking + increment views (global and other limits)

           => return nil
