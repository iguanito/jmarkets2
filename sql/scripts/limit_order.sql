select ps.period_id, ob.time_entry, ob.subject_id, s.security_name, ob.offer_type, sp.price_level, ob.offer_units, sum(tt.units_changed), ob.id from period_securities ps, securities s, security_pricelevels sp, offer_book ob left join ticker_tape tt on ob.id=tt.offer_book_id where ps.session_id=8 and ob.pricelevel_id=sp.id and sp.period_security_id=ps.id and ps.security_id=s.id group by ob.id;
