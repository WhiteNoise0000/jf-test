INSERT INTO 漁船 VALUES ('0001', '漁船A'), ('0002', '漁船B'), ('0003', '漁船C') ON CONFLICT DO NOTHING;
INSERT INTO 魚種 (名称) VALUES ('キハダ'), ('カンパチ'), ('タイ') ON CONFLICT DO NOTHING;
